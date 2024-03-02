package com.mineinabyss.launchy.logic

import com.mineinabyss.launchy.data.Dirs
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import java.nio.file.Path
import java.util.UUID
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.io.path.*

object Downloader {
    val httpClient = HttpClient()

    suspend fun downloadAvatar(uuid: UUID) {
        download("https://crafatar.com/avatars/$uuid?size=128&overlay", Dirs.avatar(uuid))
    }

    suspend fun download(
        url: String,
        writeTo: Path,
        onProgressUpdate: (progress: Progress) -> Unit = {},
    ) {
        try {
            val startTime = System.currentTimeMillis()
            val response = httpClient.get<HttpStatement>(url) {
                onDownload { bytesSentTotal, contentLength ->
                    onProgressUpdate(
                        Progress(
                            bytesSentTotal,
                            contentLength,
                            timeElapsed = System.currentTimeMillis() - startTime
                        )
                    )
                }
            }.receive<ByteArray>()
            writeTo.parent.createDirectories()
            if (!writeTo.exists())
                writeTo.createFile()
            writeTo.writeBytes(response)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}

data class Progress(val bytesDownloaded: Long, val totalBytes: Long, val timeElapsed : Long) {
    val percent: Float
        get() = bytesDownloaded.toFloat() / totalBytes.toFloat()
}
