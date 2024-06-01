package com.mineinabyss.launchy.downloads.data

import com.mineinabyss.launchy.core.ui.LaunchyUiState
import com.mineinabyss.launchy.instance.data.GameInstanceDataSource
import com.mineinabyss.launchy.util.*
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
import org.rauschig.jarchivelib.ArchiveFormat
import org.rauschig.jarchivelib.Archiver
import org.rauschig.jarchivelib.ArchiverFactory
import org.rauschig.jarchivelib.CompressionType
import java.nio.file.Path
import java.util.*
import kotlin.io.path.*

object Downloader {
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

            fun fileFor(instance: GameInstanceDataSource, url: String) =
                Dirs.cacheDir(instance) / "${urlToFileName(url)}.header"
        }
    }

    suspend fun checkUpdates(instance: GameInstanceDataSource, url: String): UpdateResult {
        val headers = ModifyHeaders.of(httpClient.head(url).headers)
        val cache = headers.toCacheString()
        val cacheFile = ModifyHeaders.fileFor(instance, url)
        return when {
            cacheFile.notExists() -> UpdateResult.NotCached(headers)
            cacheFile.readText() == cache -> UpdateResult.UpToDate(headers)
            else -> UpdateResult.HasUpdates(headers)
        }
    }

    fun saveHeaders(instance: GameInstanceDataSource, url: String, headers: ModifyHeaders) {
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

    /** @return Path to java executable */
    @OptIn(ExperimentalPathApi::class)
    suspend fun installJDK(
        state: LaunchyUiState,
    ): Path? {
        try {
            state.inProgressTasks["installJDK"] = InProgressTask("Downloading Java environment")
            val arch = Arch.get().openJDKArch
            val os = OS.get().openJDKName
            val url = "https://api.adoptium.net/v3/binary/latest/17/ga/$os/$arch/jre/hotspot/normal/eclipse"
            val javaInstallation = when (OS.get()) {
                OS.WINDOWS -> JavaInstallation(
                    url,
                    "bin/java.exe",
                    ArchiverFactory.createArchiver(ArchiveFormat.ZIP)
                )

                OS.MAC -> JavaInstallation(
                    url,
                    "Contents/Home/bin/java",
                    ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP)
                )

                OS.LINUX -> JavaInstallation(
                    url,
                    "bin/java",
                    ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP)
                )
            }
            val downloadTo = Dirs.jdks / "openjdk-17${javaInstallation.archiver.filenameExtension}"
            val extractTo = Dirs.jdks / "openjdk-17"

            val existingInstall = extractTo.resolve(javaInstallation.relativeJavaExecutable)
            if (existingInstall.exists()) return existingInstall
            download(javaInstallation.url, downloadTo, Options(
                onProgressUpdate = {
                    state.inProgressTasks["installJDK"] =
                        InProgressTask.bytes(
                            "Downloading Java environment",
                            it.bytesDownloaded,
                            it.totalBytes
                        )
                }
            ))
            state.inProgressTasks["installJDK"] = InProgressTask("Extracting Java environment")

            // Handle a case where the extraction failed and the folder exists but not the java executable
            extractTo.takeIf { it.exists() }?.deleteRecursively()
            javaInstallation.archiver.extract(downloadTo.toFile(), extractTo.toFile())
            val entries = extractTo.listDirectoryEntries()
            val jrePath = if (entries.size == 1) entries.first() else extractTo
            downloadTo.deleteIfExists()
            return jrePath / javaInstallation.relativeJavaExecutable
        } finally {
            state.inProgressTasks.remove("installJDK")
        }
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
        val saveModifyHeadersFor: GameInstanceDataSource? = null,
    )
}
