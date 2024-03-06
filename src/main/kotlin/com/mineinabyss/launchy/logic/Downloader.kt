package com.mineinabyss.launchy.logic

import com.mineinabyss.launchy.data.Dirs
import com.mineinabyss.launchy.state.InProgressTask
import com.mineinabyss.launchy.state.LaunchyState
import com.mineinabyss.launchy.ui.screens.Dialog
import com.mineinabyss.launchy.ui.screens.dialog
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
    val cacheDir = Dirs.config / "cache"
    val httpClient = HttpClient(CIO) {
        install(HttpTimeout)
    }

    suspend fun downloadAvatar(uuid: UUID) {
        download("https://crafatar.com/avatars/$uuid?size=16&overlay", Dirs.avatar(uuid))
    }

    suspend fun download(
        url: String,
        writeTo: Path,
        onFinishDownloadWhenChanged: () -> Unit = {},
        onProgressUpdate: (progress: Progress) -> Unit = {},
    ): Result<Unit> {
        return runCatching {
            val startTime = System.currentTimeMillis()
            writeTo.createParentDirectories()
            val headers = httpClient.head(url).headers
            val lastModified = headers["Last-Modified"]?.fromHttpToGmtDate()
            val length = headers["Content-Length"]?.toLongOrNull()
            val cache = "Last-Modified: $lastModified, Content-Length: $length"
            val cacheFile = cacheDir / "${writeTo.name}.cache"
            if (writeTo.exists() && cacheFile.exists() && cacheFile.readText() == cache) return@runCatching
            cacheFile.createParentDirectories()
            cacheFile.deleteIfExists()
            cacheFile.createFile().writeText(cache)

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
                onFinishDownloadWhenChanged()
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
            val javaInstallation = when (OS.get()) {
                OS.WINDOWS -> JavaInstallation(
                    "https://download.oracle.com/graalvm/17/latest/graalvm-jdk-17_windows-x64_bin.zip",
                    "bin/java.exe",
                    ArchiverFactory.createArchiver(ArchiveFormat.ZIP)
                )

                OS.MAC -> JavaInstallation(
                    "https://download.oracle.com/graalvm/17/latest/graalvm-jdk-17_macos-x64_bin.tar.gz",
                    "Contents/Home/bin/java",
                    ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP)
                )

                OS.LINUX -> JavaInstallation(
                    "https://download.oracle.com/graalvm/17/latest/graalvm-jdk-17_linux-x64_bin.tar.gz",
                    "bin/java",
                    ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP)
                )
            }
            val existingInstall = findGraalvmExtractedPath()?.resolve(javaInstallation.relativeJavaExecutable)
            if (existingInstall?.exists() == true) return existingInstall
            download(javaInstallation.url, Dirs.jdkGraal, onProgressUpdate = {
                state.inProgressTasks["installJDK"] =
                    InProgressTask.WithPercentage(
                        "Downloading Java environment",
                        it.bytesDownloaded,
                        it.totalBytes,
                        "MB"
                    )
            })
            state.inProgressTasks["installJDK"] = InProgressTask("Extracting Java environment")

            // Handle a case where the extraction failed and the folder exists but not the java executable
            findGraalvmExtractedPath()?.takeIf { it.exists() }?.deleteRecursively()
            javaInstallation.archiver.extract(Dirs.jdkGraal.toFile(), Dirs.jdks.toFile())
            return (findGraalvmExtractedPath() ?: return null) / javaInstallation.relativeJavaExecutable
        } finally {
            state.inProgressTasks.remove("installJDK")
        }
    }

    fun findGraalvmExtractedPath() = Dirs.jdks
        .listDirectoryEntries()
        .firstOrNull { it.isDirectory() && it.name.startsWith("graalvm-jdk-17") }
}

data class Progress(val bytesDownloaded: Long, val totalBytes: Long, val timeElapsed: Long) {
    val percent: Float
        get() = bytesDownloaded.toFloat() / totalBytes.toFloat()
}
