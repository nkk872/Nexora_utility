package com.nexora.utility

import android.content.Context
import android.net.Uri
import android.util.Base64
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.KeyStore
import java.util.UUID
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Encrypted-at-rest private file storage.
 *
 * Original SAF files are never deleted. The AES key is non-exportable and held by
 * Android Keystore. A vault item is only considered saved after the GCM tag verifies.
 */
class VaultStore(private val context: Context) {
    data class Entry(val id: String, val encryptedBytes: Long, val displayName: String = "Encrypted file", val mimeType: String? = null)

    private val directory: File
        get() = File(context.filesDir, "private_vault").apply { mkdirs() }

    /** Removes plaintext leftovers if Android killed the app during a previous export or preview. */
    fun cleanupTemporaryFiles(): Int {
        val leftovers = context.cacheDir.listFiles()
            .orEmpty()
            .filter { it.isFile && it.name.startsWith("vault-") && it.name.endsWith(".tmp") }
        return leftovers.count { it.delete() }
    }

    fun entries(): List<Entry> = directory.listFiles()
        .orEmpty()
        .filter { it.isFile && it.extension == "nexora" }
        .map { file -> val id = file.nameWithoutExtension; val meta = readMetadata(id); Entry(id, file.length(), meta.first ?: "Encrypted file", meta.second) }
        .sortedBy { it.displayName.lowercase() }

    fun encrypt(uri: Uri): Entry {
        val id = UUID.randomUUID().toString()
        val target = File(directory, "$id.nexora")
        val displayName = context.contentResolver.query(uri, arrayOf(android.provider.OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor -> if (cursor.moveToFirst()) cursor.getString(0) else null }?.take(160) ?: "Imported file"
        val mimeType = context.contentResolver.getType(uri)
        val cipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        }
        try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(target).use { fileOut ->
                    fileOut.write(MAGIC)
                    fileOut.write(cipher.iv.size)
                    fileOut.write(cipher.iv)
                    CipherOutputStream(fileOut, cipher).use { encryptedOut ->
                        input.copyTo(encryptedOut)
                    }
                }
            } ?: error("Unable to open the selected file")
            writeMetadata(id, displayName, mimeType)
            return Entry(id, target.length(), displayName, mimeType)
        } catch (error: Exception) {
            target.delete()
            File(directory, "$id.meta").delete()
            throw error
        }
    }

    fun encryptBytes(bytes: ByteArray): Entry {
        val id = UUID.randomUUID().toString()
        val target = File(directory, "$id.nexora")
        val cipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        }
        try {
            FileOutputStream(target).use { fileOut ->
                fileOut.write(MAGIC)
                fileOut.write(cipher.iv.size)
                fileOut.write(cipher.iv)
                CipherOutputStream(fileOut, cipher).use { encryptedOut ->
                    encryptedOut.write(bytes)
                }
            }
            return Entry(id, target.length())
        } catch (error: Exception) {
            target.delete()
            throw error
        }
    }

    /** Decrypts to app-private cache; caller should share/open then remove the temp file. */
    fun decryptToCache(id: String): File {
        require(id.matches(Regex("[a-f0-9-]{36}"))) { "Invalid vault item" }
        val source = File(directory, "$id.nexora")
        require(source.isFile) { "Vault item not found" }
        val output = File(context.cacheDir, "vault-$id.tmp")
        try {
            DataInputStream(FileInputStream(source)).use { input ->
                val magic = ByteArray(MAGIC.size)
                input.readFully(magic)
                require(magic.contentEquals(MAGIC)) { "Invalid vault file" }
                val ivLength = input.readUnsignedByte()
                require(ivLength in 12..16) { "Invalid encryption header" }
                val iv = ByteArray(ivLength)
                input.readFully(iv)
                val cipher = Cipher.getInstance(TRANSFORMATION).apply {
                    init(Cipher.DECRYPT_MODE, getOrCreateKey(), GCMParameterSpec(128, iv))
                }
                FileOutputStream(output).use { fileOut ->
                    CipherInputStream(input, cipher).use { decryptedIn ->
                        decryptedIn.copyTo(fileOut)
                    }
                }
            }
            return output
        } catch (error: Exception) {
            output.delete()
            throw error
        }
    }

    fun delete(id: String): Boolean {
        require(id.matches(Regex("[a-f0-9-]{36}"))) { "Invalid vault item" }
        val deleted = File(directory, "$id.nexora").delete()
        File(directory, "$id.meta").delete()
        return deleted
    }

    fun rename(id: String, newName: String): Boolean {
        require(id.matches(Regex("[a-f0-9-]{36}"))) { "Invalid vault item" }
        val safeName = newName.trim().take(160)
        require(safeName.isNotBlank()) { "Name cannot be empty" }
        if (!File(directory, id + ".nexora").isFile) return false
        val old = readMetadata(id)
        writeMetadata(id, safeName, old.second)
        return true
    }

    private fun writeMetadata(id: String, name: String, mime: String?) {
        val cipher = Cipher.getInstance(TRANSFORMATION).apply { init(Cipher.ENCRYPT_MODE, getOrCreateKey()) }
        DataOutputStream(FileOutputStream(File(directory, "$id.meta"))).use { output ->
            output.writeByte(cipher.iv.size)
            output.write(cipher.iv)
            val encrypted = cipher.doFinal((name + "\u0000" + (mime ?: "")).toByteArray(Charsets.UTF_8))
            output.writeInt(encrypted.size)
            output.write(encrypted)
        }
    }

    private fun readMetadata(id: String): Pair<String?, String?> {
        val source = File(directory, "$id.meta")
        if (!source.isFile) return null to null
        return runCatching {
            DataInputStream(FileInputStream(source)).use { input ->
                val ivLength = input.readUnsignedByte()
                require(ivLength in 12..16)
                val iv = ByteArray(ivLength).also(input::readFully)
                val size = input.readInt()
                require(size in 16..4096)
                val encrypted = ByteArray(size).also(input::readFully)
                val cipher = Cipher.getInstance(TRANSFORMATION).apply { init(Cipher.DECRYPT_MODE, getOrCreateKey(), GCMParameterSpec(128, iv)) }
                val values = cipher.doFinal(encrypted).toString(Charsets.UTF_8).split('\u0000', limit = 2)
                values[0].take(160) to values.getOrNull(1)?.takeIf { it.isNotBlank() }
            }
        }.getOrElse { null to null }
    }

    private fun getOrCreateKey(): SecretKey {
        val store = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (store.getKey(KEY_ALIAS, null) as? SecretKey)?.let { return it }
        val generator = KeyGenerator.getInstance("AES", "AndroidKeyStore")
        val spec = android.security.keystore.KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            android.security.keystore.KeyProperties.PURPOSE_ENCRYPT or
                android.security.keystore.KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(android.security.keystore.KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(android.security.keystore.KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()
        generator.init(spec)
        return generator.generateKey()
    }

    companion object {
        private const val KEY_ALIAS = "nexora_private_vault_aes"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private val MAGIC = byteArrayOf(0x4E, 0x58, 0x56, 0x4C, 0x54, 0x30, 0x31, 0x00)
    }
}
