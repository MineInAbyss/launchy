package com.mineinabyss.launchy.downloads.data

import com.mineinabyss.launchy.instance.data.InstanceModel
import com.mineinabyss.launchy.util.Dirs
import com.mineinabyss.launchy.util.Progress
import com.mineinabyss.launchy.util.UpdateResult
import com.mineinabyss.launchy.util.urlToFileName
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.serialization.json.Json
import org.rauschig.jarchivelib.Archiver
import java.nio.file.Path
import java.util.*
import kotlin.io.path.*

class Downloader {
    val httpClient = HttpClient(CIO) {
        install(HttpTimeout)
        install(ContentNegotiation) {
            json(json = Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    data class ModifyHeaders(val lastModified: String, val contentLength: Long) {
        fun toCacheString() = "Last-Modified: $lastModified, Content-Length: $contentLength"

        companion object {
            @OptIn(ExperimentalStdlibApi::class)
            fun of(headers: Headers) = ModifyHeaders(
                headers["Last-Modified"]?.fromHttpToGmtDate()?.timestamp?.toHexString() ?: "",
                headers["Content-Length"]?.toLongOrNull() ?: 0
            )

            fun fileFor(instance: InstanceModel, url: String) =
                Dirs.cacheDir(instance) / "${urlToFileName(url)}.header"
        }
    }

    suspend fun checkUpdates(instance: InstanceModel, url: String): UpdateResult {
        val headers = ModifyHeaders.of(httpClient.head(url).headers)
        val cache = headers.toCacheString()
        val cacheFile = ModifyHeaders.fileFor(instance, url)
        return when {
            cacheFile.notExists() -> UpdateResult.NotCached(headers)
            cacheFile.readText() == cache -> UpdateResult.UpToDate(headers)
            else -> UpdateResult.HasUpdates(headers)
        }
    }

    fun saveHeaders(instance: InstanceModel, url: String, headers: ModifyHeaders) {
        ModifyHeaders.fileFor(instance, url).createParentDirectories().apply {
            deleteIfExists()
            createFile()
            writeText(headers.toCacheString())
        }
    }

    sealed class DownloadResult {
        data class Success(val modifyHeaders: ModifyHeaders) : DownloadResult()

        data object AlreadyExists : DownloadResult()
    }

    suspend fun download(
        url: String,
        writeTo: Path,
        options: Options = Options(),
    ): Result<DownloadResult> {
        return runCatching {
            if (!options.overwrite && writeTo.exists()) return@runCatching DownloadResult.AlreadyExists
            val startTime = System.currentTimeMillis()
            writeTo.createParentDirectories()

            httpClient.prepareGet(url) {
                timeout {
                    requestTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS
                }
                onDownload { bytesSentTotal, contentLength ->
                    options.onProgressUpdate(
                        Progress(
                            bytesSentTotal,
                            contentLength,
                            timeElapsed = System.currentTimeMillis() - startTime
                        )
                    )
                }
            }.execute { httpResponse ->
                val modifyHeaders = ModifyHeaders.of(httpResponse.headers)

                if (options.saveModifyHeadersFor != null) {
                    saveHeaders(options.saveModifyHeadersFor, url, modifyHeaders)
                }

                writeTo.deleteIfExists()
                writeTo.createFile()
                val channel: ByteReadChannel = httpResponse.body()
                while (!channel.isClosedForRead) {
                    val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
                    while (!packet.isEmpty) {
                        val bytes = packet.readBytes()
                        writeTo.appendBytes(bytes)
                    }
                }
                options.whenChanged()
                DownloadResult.Success(modifyHeaders)
            }
        }.onFailure {
            it.printStackTrace()
        }
    }

    suspend fun downloadAvatar(uuid: UUID, options: Options) {
        download("https://mc-heads.net/avatar/$uuid", Dirs.avatar(uuid), options)
    }

    class JavaInstallation(
        val url: String,
        val relativeJavaExecutable: String,
        val archiver: Archiver,
    )

    data class Options(
        val overwrite: Boolean = true,
        val whenChanged: () -> Unit = {},
        val onProgressUpdate: (progress: Progress) -> Unit = {},
        val saveModifyHeadersFor: InstanceModel? = null,
    )
}
