package com.mineinabyss.launchy.logic

import com.mineinabyss.launchy.data.config.DownloadInfo
import com.mineinabyss.launchy.data.config.HashCheck
import com.mineinabyss.launchy.data.modpacks.InstanceModLoaders
import com.mineinabyss.launchy.data.modpacks.Mod
import com.mineinabyss.launchy.state.InProgressTask
import com.mineinabyss.launchy.state.LaunchyState
import com.mineinabyss.launchy.state.modpack.ModpackState
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.Serializable
import java.util.concurrent.CancellationException
import kotlin.io.path.*

object ModDownloader {

    suspend fun ModpackState.installMCAndModLoaders(state: LaunchyState, modLoaders: InstanceModLoaders) {
        state.runTask(Tasks.installModLoadersId, InProgressTask("Installing ${modLoaders.fabricLoader}")) {
            Launcher.download(
                modLoaders,
                instance.minecraftDir,
                onFinishDownload = { println("Finished installing: $it") },
            ).join()
        }
    }

    @Serializable
    sealed interface DownloadResult {
        @Serializable
        data object Success : DownloadResult

        @Serializable
        data object Failed : DownloadResult
    }

    suspend fun ModpackState.download(mod: Mod, ignoreCachedCheck: Boolean): DownloadResult {
        val name = mod.info.name
        try {
            println("Starting download of $name")
            downloads.inProgressMods[mod] = Progress(0, 0, 0) // set progress to 0
            Downloader.download(
                url = mod.info.url,
                writeTo = mod.absoluteDownloadDest,
                skipDownloadIfCached = !ignoreCachedCheck
            ) progress@{
                downloads.inProgressMods[mod] = it
            }
            return DownloadResult.Success
        } catch (ex: CancellationException) {
            throw ex // Must let the CancellationException propagate
        } catch (e: Exception) {
            println("Failed to download $name")
            e.printStackTrace()
            return DownloadResult.Failed
        } finally {
            println("Finished download of $name")
            downloads.inProgressMods -= mod
        }
    }

//        if (mod.info.configUrl.isNotBlank() && (mod in toggles.enabledConfigs) && mod !in toggles.upToDateConfigs) {
//            try {
//                println("Starting download of $name config")
//                downloads.inProgressConfigs[mod] = Progress(0, 0, 0) // set progress to 0
//                val config = mod.config
//                Downloader.download(url = mod.info.configUrl, writeTo = config) {
//                    downloads.inProgressConfigs[mod] = it
//                }
//                toggles.downloadConfigURLs[mod] = mod.info.configUrl
//                ArchiverFactory.createArchiver(config.extension)
//                    .extract(config.toFile(), instance.overridesDir.toFile())
//                config.deleteIfExists()
//                saveToConfig()
//                println("Successfully downloaded $name config")
//            } catch (ex: CancellationException) {
//                throw ex // Must let the CancellationException propagate
//            } catch (e: Exception) {
//                println("Failed to download $name config")
//                downloads.failed += mod
//                e.printStackTrace()
//            } finally {
//                println("Finished download of $name config")
//                downloads.inProgressConfigs -= mod
//            }
//        }

    /**
     * Ensures dependencies the user definitely wants are installed,
     * does not install any mod updates or new dep versions if they changed in the modpack.
     * Primarily the mod loader/minecraft version.
     */
    suspend fun ModpackState.ensureDependenciesReady(state: LaunchyState) = coroutineScope {
        val currentDeps = userAgreedDeps
        if (currentDeps == null) {
            userAgreedDeps = modpack.modLoaders
        }
        installMCAndModLoaders(state, currentDeps ?: modpack.modLoaders)
    }

    fun ModpackState.copyMods() {
        // Clear mods folder
        val existingEntries = instance.modsDir.useDirectoryEntries { files ->
            files.filter { !it.isDirectory() }.toList()
        }

        val userMods = instance.userMods.listDirectoryEntries("*.jar")
            .map { it.absolute() to Path("mods") / it.relativeTo(instance.userMods) }
        val downloadedMods = toggles.enabledMods
            .map { it.absoluteDownloadDest to it.absoluteDownloadDest.relativeTo(instance.downloadsDir) }
        val linked = (downloadedMods + userMods)
            .map { (absolute, relative) ->
                val linkDest = (instance.minecraftDir / relative)
                if (!linkDest.isSymbolicLink()) linkDest.deleteIfExists()
                if (linkDest.notExists())
                    linkDest.createLinkPointingTo(absolute.relativeTo(linkDest.parent))
                linkDest
            }
            .toSet()

        (existingEntries - linked).forEach { it.deleteIfExists() }
    }

    suspend fun ModpackState.prepareWithoutChangingInstalledMods(state: LaunchyState) {
        ensureDependenciesReady(state)
        copyMods()
    }

    @OptIn(ExperimentalPathApi::class)
    fun ModpackState.copyOverrides(state: LaunchyState) {
        state.runTask(Tasks.copyOverridesId, InProgressTask("Copying overrides")) {
            modpack.overridesPaths.forEach {
                it.copyToRecursively(
                    target = instance.minecraftDir,
                    followLinks = false,
                    overwrite = true,
                )
            }
        }
    }

    /**
     * Updates mod loader versions and mods to latest modpack definition.
     */
    suspend fun ModpackState.startInstall(state: LaunchyState, ignoreCachedCheck: Boolean = false) = coroutineScope {
        userAgreedDeps = modpack.modLoaders
        ensureDependenciesReady(state)
        copyOverrides(state)

        queued.deletions.forEach {
            queued.modDownloadInfo.remove(it.modId)
        }

        val downloads = queued.needsInstall.map { mod ->
            async(AppDispatchers.IOContext) {
                mod to download(mod, ignoreCachedCheck)
            }
        }.awaitAll()

        downloads.forEach { (mod, result) ->
            queued.modDownloadInfo[mod.modId] = DownloadInfo(
                url = mod.downloadUrl.toString(),
                path = mod.absoluteDownloadDest.relativeTo(instance.minecraftDir).toString(),
                desiredHash = mod.desiredHashes?.sha1,
                hashCheck = HashCheck.UNKNOWN,
                result = result
            )
        }

        // Check hashes
        val updatedHashes = queued.modDownloadInfo
            .filterValues { it.hashCheck == HashCheck.UNKNOWN || it.hashCheck == HashCheck.FAILED }
            .map { (modId, info) ->
                async(AppDispatchers.IOContext) {
                    val check = runCatching { info.calculateSha1Hash(instance.minecraftDir) }.getOrNull()
                    modId to info.copy(
                        hashCheck = when {
                            check == info.desiredHash -> HashCheck.VERIFIED
                            else -> HashCheck.FAILED
                        }
                    )
                }
            }.awaitAll()

        updatedHashes.forEach { (modId, newInfo) ->
            queued.modDownloadInfo[modId] = newInfo
        }

        saveToConfig()

        if (queued.modDownloadInfo.any { it.value.hashCheck != HashCheck.VERIFIED }) {
            error("Hash check failed on one or more downloads downloads, please re-run the installer!")
        }

        copyMods()

        saveToConfig()
    }
}
