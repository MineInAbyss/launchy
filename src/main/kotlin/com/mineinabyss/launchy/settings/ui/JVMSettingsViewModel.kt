package com.mineinabyss.launchy.settings.ui

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mineinabyss.launchy.config.data.Config
import com.mineinabyss.launchy.core.data.TasksRepository
import com.mineinabyss.launchy.downloads.data.Downloader
import com.mineinabyss.launchy.downloads.data.Downloader.JavaInstallation
import com.mineinabyss.launchy.downloads.data.Downloader.Options
import com.mineinabyss.launchy.util.*
import kotlinx.coroutines.launch
import org.rauschig.jarchivelib.ArchiveFormat
import org.rauschig.jarchivelib.ArchiverFactory
import org.rauschig.jarchivelib.CompressionType
import kotlin.io.path.*

class JVMSettingsViewModel(
    val config: Config,
    val tasks: TasksRepository,
    val downloader: Downloader,
) : ViewModel() {
    var javaPath by mutableStateOf(config.javaPath?.let { Path(it) })
    var userMemoryAllocation by mutableStateOf(config.memoryAllocation)
    var userJvmArgs by mutableStateOf(config.jvmArguments)
    var useRecommendedJvmArgs by mutableStateOf(config.useRecommendedJvmArguments)
    val suggestedArgs
        get() = buildString {
            if ("graalvm" in javaPath.toString()) {
                append(SuggestedJVMArgs.graalVMBaseFlags)
            } else {
                append(SuggestedJVMArgs.baseFlags)
            }
            append(" ")
            append(SuggestedJVMArgs.clientG1GC)
        }
    val jvmArgs by derivedStateOf {
        val memory = (userMemoryAllocation ?: SuggestedJVMArgs.memory).toString()

        "-Xms${memory}M -Xmx${memory}M ${userJvmArgs?.takeIf { !useRecommendedJvmArgs } ?: suggestedArgs}"
    }
    val memory get() = userMemoryAllocation ?: SuggestedJVMArgs.memory


    /** @return Path to java executable */
    @OptIn(ExperimentalPathApi::class)
    fun installJDK() = viewModelScope.launch(AppDispatchers.IO) {
        try {
            tasks.start("installJDK", InProgressTask("Downloading Java environment"))
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
            if (existingInstall.exists()) return@launch
            downloader.download(javaInstallation.url, downloadTo, Options(
                onProgressUpdate = {
                    tasks.start(
                        "installJDK",
                        InProgressTask.bytes(
                            "Downloading Java environment",
                            it.bytesDownloaded,
                            it.totalBytes
                        )
                    )
                }
            ))
            tasks.start("installJDK", InProgressTask("Extracting Java environment"))

            // Handle a case where the extraction failed and the folder exists but not the java executable
            extractTo.takeIf { it.exists() }?.deleteRecursively()
            javaInstallation.archiver.extract(downloadTo.toFile(), extractTo.toFile())
            val entries = extractTo.listDirectoryEntries()
            val jrePath = if (entries.size == 1) entries.first() else extractTo
            downloadTo.deleteIfExists()

            javaPath = jrePath / javaInstallation.relativeJavaExecutable
//                dialog = Dialog.Error(
//                    "Failed to install Java",
//                    "Please install Java manually and select the path in settings."
//                )
        } finally {
            tasks.finish("installJDK")
        }
    }
}
