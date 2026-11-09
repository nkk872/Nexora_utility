package com.nexora.utility

import android.content.ContentResolver
import android.net.Uri
import java.security.MessageDigest

/** Local, content-based duplicate grouping. It never modifies or deletes source files. */
object DuplicateScanner {
    data class Candidate(val uri: Uri, val displayName: String, val sizeBytes: Long)
    data class DuplicateGroup(val sha256: String, val items: List<Candidate>)

    fun findDuplicates(
        resolver: ContentResolver,
        candidates: List<Candidate>,
        maxFiles: Int = 2_000,
        maxBytesPerFile: Long = 250L * 1024L * 1024L
    ): List<DuplicateGroup> {
        val hashes = linkedMapOf<String, MutableList<Candidate>>()
        candidates.asSequence()
            .filter { it.sizeBytes > 0 && it.sizeBytes <= maxBytesPerFile }
            .take(maxFiles)
            .forEach { candidate ->
                val digest = MessageDigest.getInstance("SHA-256")
                resolver.openInputStream(candidate.uri)?.use { input ->
                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                    while (true) {
                        val count = input.read(buffer)
                        if (count < 0) break
                        digest.update(buffer, 0, count)
                    }
                } ?: return@forEach
                val hash = digest.digest().joinToString("") { "%02x".format(it) }
                hashes.getOrPut(hash) { mutableListOf() }.add(candidate)
            }
        return hashes.entries
            .filter { it.value.size > 1 }
            .map { DuplicateGroup(it.key, it.value.toList()) }
    }
}
