package com.mineinabyss.launchy.util.hashing

import java.io.InputStream
import java.nio.file.Path
import java.security.MessageDigest
import kotlin.io.path.inputStream

object Hashing {
    const val STREAM_BUFFER_LENGTH = 1024

    @OptIn(ExperimentalStdlibApi::class)
    fun Path.checksum(digest: MessageDigest): String = inputStream().use { stream ->
        updateDigest(digest, stream).digest().toHexString()
    }

    private fun updateDigest(digest: MessageDigest, data: InputStream): MessageDigest {
        val buffer = ByteArray(STREAM_BUFFER_LENGTH)
        var read = data.read(buffer, 0, STREAM_BUFFER_LENGTH)
        while (read > -1) {
            digest.update(buffer, 0, read)
            read = data.read(buffer, 0, STREAM_BUFFER_LENGTH)
        }
        return digest
    }
}
