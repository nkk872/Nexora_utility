package com.nexora.utility

import android.content.ContentResolver
import android.net.Uri
import java.security.MessageDigest

/** Read-only, content-based duplicate grouping. Source files are never modified. */
object DuplicateScanner {
    data class Candidate(val uri: Uri, val displayName: String, val sizeBytes: Long)
    data class DuplicateGroup(val sha256: String, val items: List<Candidate>)

    /**
     * First narrows candidates by reported size, then hashes only files whose size is shared.
     * Unreadable or changed documents are skipped rather than aborting the whole scan.
     */
    fun findDuplicates(
        resolver: ContentResolver,
        candidates: List<Candidate>,
        maxFiles: Int = 2_000,
        maxBytesPerFile: Long = 250L * 1024L * 1024L
    ): List<DuplicateGroup> {
        require(maxFiles > 0) { "maxFiles must be positive" }
        require(maxBytesPerFile > 0) { "maxBytesPerFile must be positive" }

        val eligible = candidates.asSequence()
            .distinctBy { it.uri.toString() }
            .filter { it.sizeBytes > 0L && it.sizeBytes <= maxBytesPerFile }
            .take(maxFiles)
            .toList()

        val possibleDuplicates = eligible
            .groupBy { it.sizeBytes }
            .values
            .filter { it.size > 1 }
            .flatten()

        val hashes = linkedMapOf<String, MutableList<Candidate>>()
        possibleDuplicates.forEach { candidate ->
            val hash = runCatching {
                val digest = MessageDigest.getInstance("SHA-256")
                val actualBytes = resolver.openInputStream(candidate.uri)?.use { input ->
                    val buffer = ByteArray(64 * 1024)
                    var total = 0L
                    while (true) {
                        val count = input.read(buffer)
                        if (count < 0) break
                        total += count
                        // Protect against a provider returning more data than the scanned metadata reported.
                        if (total > maxBytesPerFile || total > candidate.sizeBytes) {
                            error("Document size changed during duplicate scan")
                        }
                        digest.update(buffer, 0, count)
                    }
                    total
                } ?: error("Document is no longer readable")

                if (actualBytes != candidate.sizeBytes) error("Document size changed during duplicate scan")
                digest.digest().joinToString("") { byte -> "%02x".format(byte) }
            }.getOrNull() ?: return@forEach

            hashes.getOrPut(hash) { mutableListOf() }.add(candidate)
        }

        return hashes.entries
            .filter { it.value.size > 1 }
            .map { DuplicateGroup(it.key, it.value.toList()) }
    }
}
