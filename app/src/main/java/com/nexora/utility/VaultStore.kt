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
    data class Entry(val id: String, val encryptedBytes: Long)

    private val directory: File
        get() = File(context.filesDir, "private_vault").apply { mkdirs() }

    fun entries(): List<Entry> = directory.listFiles()
        .orEmpty()
        .filter { it.isFile && it.extension == "nexora" }
        .map { Entry(it.nameWithoutExtension, it.length()) }
        .sortedBy { it.id }

    fun encrypt(uri: Uri): Entry {
        val id = UUID.randomUUID().toString()
        val target = File(directory, "$id.nexora")
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
        return File(directory, "$id.nexora").delete()
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
