package com.mineinabyss.launchy.logic

import com.mineinabyss.launchy.data.Dirs
import com.mineinabyss.launchy.state.InProgressTask
import com.mineinabyss.launchy.state.LaunchyState
import com.mineinabyss.launchy.util.Arch
import com.mineinabyss.launchy.util.OS
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
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
    }

    suspend fun downloadAvatar(uuid: UUID) {
        download("https://mc-heads.net/avatar/$uuid", Dirs.avatar(uuid))
    }

    class CacheInfo(val result: UpdateResult, val cacheKey: String, val cacheFile: Path)

    @OptIn(ExperimentalStdlibApi::class)
    suspend fun checkUpdates(url: String): CacheInfo {
        val headers = httpClient.head(url).headers
        val lastModified = headers["Last-Modified"]?.fromHttpToGmtDate()?.timestamp?.toHexString()
        val length = headers["Content-Length"]?.toLongOrNull()?.toHexString()
        val cache = "Last-Modified: $lastModified, Content-Length: $length"
        val cacheFile = Dirs.cacheDir / "${urlToFileName(url)}.cache"
        val result = when {
            cacheFile.notExists() -> UpdateResult.NotCached
            cacheFile.readText() == cache -> UpdateResult.UpToDate
            else -> UpdateResult.HasUpdates
        }
        return CacheInfo(result, cache, cacheFile)
    }

    suspend fun download(
        url: String,
        writeTo: Path,
        override: Boolean = true,
        skipDownloadIfCached: Boolean = true,
        whenChanged: () -> Unit = {},
        onProgressUpdate: (progress: Progress) -> Unit = {},
    ): Result<Unit> {
        return runCatching {
            if (!override && writeTo.exists()) return@runCatching
            val startTime = System.currentTimeMillis()
            writeTo.createParentDirectories()
            if (skipDownloadIfCached) {
                val updates = checkUpdates(url)
                if (writeTo.exists() && updates.result == UpdateResult.UpToDate) return@runCatching
                updates.cacheFile.apply {
                    createParentDirectories()
                    deleteIfExists()
                    createFile().writeText(updates.cacheKey)
                }
            }

            httpClient.prepareGet(url) {
                timeout {
                    requestTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS
                }
                onDownload { bytesSentTotal, contentLength ->
                    onProgressUpdate(
                        Progress(
                            bytesSentTotal,
                            contentLength,
                            timeElapsed = System.currentTimeMillis() - startTime
                        )
                    )
                }
            }.execute { httpResponse ->
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
                whenChanged()
            }
        }.onFailure {
            it.printStackTrace()
        }
    }

    class JavaInstallation(
        val url: String,
        val relativeJavaExecutable: String,
        val archiver: Archiver,
    )

    /** @return Path to java executable */
    @OptIn(ExperimentalPathApi::class)
    suspend fun installJDK(
        state: LaunchyState,
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
            download(javaInstallation.url, downloadTo, onProgressUpdate = {
                state.inProgressTasks["installJDK"] =
                    InProgressTask.bytes(
                        "Downloading Java environment",
                        it.bytesDownloaded,
                        it.totalBytes
                    )
            })
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
}

data class Progress(val bytesDownloaded: Long, val totalBytes: Long, val timeElapsed: Long) {
    val percent: Float
        get() = bytesDownloaded.toFloat() / totalBytes.toFloat()
}
