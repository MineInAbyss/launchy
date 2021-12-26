package com.mineinabyss.launchy.logic

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import java.nio.file.Path
import kotlin.io.path.writeBytes

object Downloader {
    val httpClient = HttpClient()

    suspend fun download(
        url: String,
        writeTo: Path,
        onProgressUpdate: (progress: Long) -> Unit = {},
    ) {
        val response = httpClient.get<HttpStatement>(url) {
            onDownload { bytesSentTotal, contentLength ->
                val progress = bytesSentTotal / contentLength
                onProgressUpdate(progress)
            }

        }.receive<ByteArray>()
        writeTo.writeBytes(response)
    }
}
