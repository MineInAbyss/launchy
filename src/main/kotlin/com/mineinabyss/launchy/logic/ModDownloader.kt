package com.mineinabyss.launchy.logic

import com.mineinabyss.launchy.data.modpacks.Mod
import com.mineinabyss.launchy.data.modpacks.PackDependencies
import com.mineinabyss.launchy.state.InProgressTask
import com.mineinabyss.launchy.state.LaunchyState
import com.mineinabyss.launchy.state.modpack.ModpackState
import kotlinx.coroutines.*
import org.rauschig.jarchivelib.ArchiverFactory
import java.util.concurrent.CancellationException
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.copyToRecursively
import kotlin.io.path.deleteIfExists
import kotlin.io.path.extension

object ModDownloader {
    val installModLoadersId = "installMCAndModLoaders"
    val copyOverridesId = "copyOverrides"
    suspend fun ModpackState.installMCAndModLoaders(state: LaunchyState, dependencies: PackDependencies) {
        try {
            downloads.installingProfile = true
            Launcher.download(
                dependencies,
                instance.minecraftDir,
                onStartDownload = {
                    state.inProgressTasks[installModLoadersId] = InProgressTask("Installing $it")
                },
                onFinishDownload = { println("Finished installing: $it") },
            ).join()
        } finally {
            state.inProgressTasks.remove(installModLoadersId)
            downloads.installingProfile = false
        }
    }

    suspend fun ModpackState.download(state: LaunchyState, mod: Mod) {
        val name = mod.info.name
        val taskKey = "modDownload${mod.info.url}"
        runCatching {
            if (mod !in toggles.upToDateMods) {
                try {
                    println("Starting download of $name")
                    downloads.inProgressMods[mod] = Progress(0, 0, 0) // set progress to 0
                    Downloader.download(url = mod.info.url, writeTo = mod.file) progress@{
                        downloads.inProgressMods[mod] = it
                        state.inProgressTasks[taskKey] = InProgressTask.bytes(
                            "Downloading $name", it.bytesDownloaded, it.totalBytes
                        )
                    }
                    toggles.downloadURLs[mod] = mod.info.url
                    saveToConfig()
                    println("Successfully downloaded $name")
                } catch (ex: CancellationException) {
                    throw ex // Must let the CancellationException propagate
                } catch (e: Exception) {
                    println("Failed to download $name")
                    e.printStackTrace()
                    downloads.failed += mod
                } finally {
                    println("Finished download of $name")
                    downloads.inProgressMods -= mod
                }
            }

            if (mod.info.configUrl.isNotBlank() && (mod in toggles.enabledConfigs) && mod !in toggles.upToDateConfigs) {
                try {
                    println("Starting download of $name config")
                    downloads.inProgressConfigs[mod] = Progress(0, 0, 0) // set progress to 0
                    val config = mod.config
                    Downloader.download(url = mod.info.configUrl, writeTo = config) {
                        downloads.inProgressConfigs[mod] = it
                    }
                    toggles.downloadConfigURLs[mod] = mod.info.configUrl
                    ArchiverFactory.createArchiver(config.extension)
                        .extract(config.toFile(), instance.overridesDir.toFile())
                    config.deleteIfExists()
                    saveToConfig()
                    println("Successfully downloaded $name config")
                } catch (ex: CancellationException) {
                    throw ex // Must let the CancellationException propagate
                } catch (e: Exception) {
                    println("Failed to download $name config")
                    downloads.failed += mod
                    e.printStackTrace()
                } finally {
                    println("Finished download of $name config")
                    downloads.inProgressConfigs -= mod
                }
            }
        }.onFailure {
            if (it !is CancellationException) {
                it.printStackTrace()
            }
//            Badge {
//                Text("Failed to download ${mod.name}: ${it.localizedMessage}!"/*, "OK"*/)
//            }
//            scaffoldState.snackbarHostState.showSnackbar(
//                "Failed to download ${mod.name}: ${it.localizedMessage}!", "OK"
//            )
        }
        state.inProgressTasks.remove(taskKey)
    }

    /**
     * Ensures dependencies the user definitely wants are installed,
     * does not install any mod updates or new dep versions if they changed in the modpack.
     * Primarily the mod loader/minecraft version.
     */
    suspend fun ModpackState.ensureCurrentDepsInstalled(state: LaunchyState) {
        val currentDeps = userAgreedDeps
        if (currentDeps == null) {
            userAgreedDeps = modpack.dependencies
        }
        installMCAndModLoaders(state, currentDeps ?: modpack.dependencies)
    }

    @OptIn(ExperimentalPathApi::class)
    fun ModpackState.copyOverrides(state: LaunchyState) {
        try {
            state.inProgressTasks[copyOverridesId] = InProgressTask("Copying overrides")
            modpack.overridesPaths.forEach {
                it.copyToRecursively(
                    target = instance.minecraftDir,
                    followLinks = false,
                    overwrite = true,
                )
            }
        } finally {
            state.inProgressTasks.remove(copyOverridesId)
        }
    }

    /**
     * Updates mod loader versions and mods to latest modpack definition.
     */
    suspend fun ModpackState.install(state: LaunchyState): Job = coroutineScope {
        launch {
            userAgreedDeps = modpack.dependencies
            runCatching { ensureCurrentDepsInstalled(state) }.getOrShowDialog() ?: return@launch
            runCatching { copyOverrides(state) }.getOrShowDialog() ?: return@launch
            toggles.checkNonDownloadedMods()
            val modDownloads = launch {
                queued.downloads.map { mod ->
                    state.downloadContext.launch {
                        download(state, mod)
                        toggles.checkNonDownloadedMods()
                    }
                }.joinAll()
            }
            val modDeletions = launch {
                queued.deletions.map { mod ->
                    launch(Dispatchers.IO) {
                        try {
                            mod.file.deleteIfExists()
                        } catch (e: FileSystemException) {
                            return@launch
                        } finally {
                            queued.deleted++
                        }
                    }
                }.joinAll()
            }
            modDownloads.join()
            modDeletions.join()
        }
    }
}
