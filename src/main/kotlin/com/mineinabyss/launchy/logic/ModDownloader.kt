package com.mineinabyss.launchy.logic

import com.mineinabyss.launchy.data.Dirs
import com.mineinabyss.launchy.data.config.unzip
import com.mineinabyss.launchy.data.modpacks.Mod
import com.mineinabyss.launchy.state.modpack.ModpackState
import kotlinx.coroutines.*
import java.util.concurrent.CancellationException
import kotlin.io.path.deleteIfExists

object ModDownloader {
    fun ModpackState.installMCAndModLoaders() {
        downloads.installingProfile = true
        Launcher.download(
            modpack.dependencies,
            modpackDir,
            finishedDownload = { println("Finished installing: $it") },
        )
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

    suspend fun ModpackState.install(): Job = coroutineScope {
        installMCAndModLoaders()
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
        return@coroutineScope launch { modDownloads.join(); modDeletions.join() }
    }
}
