package com.mineinabyss.launchy.logic

import com.mineinabyss.launchy.data.Dirs
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import java.nio.file.Path
import java.util.*
import kotlin.io.path.*

object Downloader {
    val cacheDir = Dirs.config / "cache"
    val httpClient = HttpClient(CIO)

    suspend fun downloadAvatar(uuid: UUID) {
        download("https://crafatar.com/avatars/$uuid?size=128&overlay", Dirs.avatar(uuid))
    }

    suspend fun download(
        url: String,
        writeTo: Path,
        onProgressUpdate: (progress: Progress) -> Unit = {},
    ) {
        runCatching {
            val startTime = System.currentTimeMillis()
            writeTo.createParentDirectories()
            if (!writeTo.exists()) writeTo.createFile()
            val headers = httpClient.head(url).headers
            val lastModified = headers["Last-Modified"]?.fromHttpToGmtDate()
            val length = headers["Content-Length"]?.toLongOrNull()
            val cache = "Last-Modified: $lastModified, Content-Length: $length"
            val cacheFile = cacheDir / "${writeTo.name}.cache"
            if (cacheFile.exists() && cacheFile.readText() == cache) return
            cacheFile.createParentDirectories()
            cacheFile.deleteIfExists()
            cacheFile.createFile().writeText(cache)

            httpClient.get(url) {
                onDownload { bytesSentTotal, contentLength ->
                    onProgressUpdate(
                        Progress(
                            bytesSentTotal,
                            contentLength,
                            timeElapsed = System.currentTimeMillis() - startTime
                        )
                    )
                }
            }.bodyAsChannel().copyAndClose(writeTo.toFile().writeChannel())
        }.onFailure {
            it.printStackTrace()
        }
    }
}

data class Progress(val bytesDownloaded: Long, val totalBytes: Long, val timeElapsed: Long) {
    val percent: Float
        get() = bytesDownloaded.toFloat() / totalBytes.toFloat()
}
