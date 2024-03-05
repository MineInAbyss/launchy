package com.mineinabyss.launchy.logic

import com.mineinabyss.launchy.data.Dirs
import com.mineinabyss.launchy.data.config.unzip
import com.mineinabyss.launchy.data.modpacks.Mod
import com.mineinabyss.launchy.data.modpacks.PackDependencies
import com.mineinabyss.launchy.state.InProgressTask
import com.mineinabyss.launchy.state.LaunchyState
import com.mineinabyss.launchy.state.modpack.DownloadState
import com.mineinabyss.launchy.state.modpack.ModpackState
import kotlinx.coroutines.*
import java.util.concurrent.CancellationException
import kotlin.io.path.deleteIfExists

object ModDownloader {
    val installModLoadersId = "installMCAndModLoaders"
    suspend fun ModpackState.installMCAndModLoaders(state: LaunchyState, dependencies: PackDependencies) {
        downloads.installingProfile = true
        Launcher.download(
            dependencies,
            instance.minecraftDir,
            onStartDownload = {
                state.inProgressTasks[installModLoadersId] = InProgressTask(it)
            },
            onFinishDownload = { println("Finished installing: $it") },
        ).join()
        state.inProgressTasks.remove(installModLoadersId)
        downloads.installingProfile = false
    }

    suspend fun ModpackState.download(mod: Mod) {
        val name = mod.info.name
        runCatching {
            if (mod !in toggles.upToDateMods) {
                try {
                    println("Starting download of $name")
                    downloads.inProgressMods[mod] = Progress(0, 0, 0) // set progress to 0
                    Downloader.download(url = mod.info.url, writeTo = mod.file) progress@{
                        downloads.inProgressMods[mod] = it
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
                    Downloader.download(url = mod.info.configUrl, writeTo = config) progress@{
                        downloads.inProgressConfigs[mod] = it
                    }
                    toggles.downloadConfigURLs[mod] = mod.info.configUrl
                    unzip(config.toFile(), Dirs.mineinabyss.toString())
                    config.toFile().delete()
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
    }

    /**
     * Ensures dependencies the user definitely wants are installed,
     * does not install any mod updates or new dep versions if they changed in the modpack.
     * Primarily the mod loader/minecraft version.
     */
    suspend fun ModpackState.ensureCurrentDepsInstalled(state: LaunchyState): Job = coroutineScope {
        launch {
            val currentDeps = userAgreedDeps
            if (currentDeps == null) {
                userAgreedDeps = modpack.dependencies
            }
            installMCAndModLoaders(state, currentDeps ?: modpack.dependencies)
        }
    }

    /**
     * Updates mod loader versions and mods to latest modpack definition.
     */
    suspend fun ModpackState.install(state: LaunchyState): Job = coroutineScope {
        launch {
            userAgreedDeps = modpack.dependencies
            ensureCurrentDepsInstalled(state).join()
            toggles.checkNonDownloadedMods()
            val modDownloads = launch {
                queued.downloads.map { mod ->
                    launch(Dispatchers.IO) {
                        download(mod)
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
