package com.mineinabyss.launchy.logic

import com.mineinabyss.launchy.data.Dirs
import com.mineinabyss.launchy.data.ModInfo
import com.mineinabyss.launchy.data.config.unzip
import com.mineinabyss.launchy.data.modpacks.Mod
import com.mineinabyss.launchy.state.modpack.SelectedModpackState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.to2mbn.jmccc.option.MinecraftDirectory
import java.util.concurrent.CancellationException
import kotlin.io.path.deleteIfExists

object ModDownloader {
    fun SelectedModpackState.installMCAndModLoaders() {
        downloads.installingProfile = true
        Launcher.download(
            modpack.dependencies,
            MinecraftDirectory(Dirs.mineinabyss.toFile()),
            finishedDownload = {
                //TODO notifs
            },
        )
        downloads.installingProfile = false
    }

    suspend fun SelectedModpackState.download(mod: Mod) {
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
    suspend fun SelectedModpackState.install() = coroutineScope {
        installMCAndModLoaders()
        toggles.checkNonDownloadedMods()
        for (mod in queued.downloads)
            launch(Dispatchers.IO) {
                download(mod)
                toggles.checkNonDownloadedMods()
            }
        for (mod in queued.deletions) {
            launch(Dispatchers.IO) {
                try {
                    mod.file.deleteIfExists()
                } catch (e: FileSystemException) {
                    return@launch
                } finally {
                    queued.deleted++
                }
            }
        }
    }
}
