package com.mineinabyss.launchy.downloads.data

import com.mineinabyss.launchy.config.data.DownloadInfo
import com.mineinabyss.launchy.config.data.HashCheck
import com.mineinabyss.launchy.core.data.Downloader
import com.mineinabyss.launchy.core.ui.LaunchyState
import com.mineinabyss.launchy.instance.data.GameInstanceState
import com.mineinabyss.launchy.instance.data.InstanceModLoaders
import com.mineinabyss.launchy.instance.data.Launcher
import com.mineinabyss.launchy.instance.data.Mod
import com.mineinabyss.launchy.util.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.Serializable
import java.util.concurrent.CancellationException
import kotlin.io.path.*

object ModDownloader {

    suspend fun GameInstanceState.installMCAndModLoaders(state: LaunchyState, modLoaders: InstanceModLoaders) {
        state.runTask(Tasks.installModLoadersId, InProgressTask("Installing ${modLoaders.fullVersionName}")) {
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

    suspend fun GameInstanceState.download(mod: Mod, overwrite: Boolean): DownloadResult {
        val name = mod.info.name
        try {
            println("Starting download of $name")
            downloads.inProgressMods[mod] = Progress(0, 0, 0) // set progress to 0
            Downloader.download(
                url = mod.info.url,
                writeTo = mod.absoluteDownloadDest,
                options = Downloader.Options(
                    overwrite = overwrite,
                    onProgressUpdate = { downloads.inProgressMods[mod] = it }
                )
            )
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

    /**
     * Ensures dependencies the user definitely wants are installed,
     * does not install any mod updates or new dep versions if they changed in the modpack.
     * Primarily the mod loader/minecraft version.
     */
    suspend fun GameInstanceState.ensureDependenciesReady(state: LaunchyState) = coroutineScope {
        val currentDeps = queued.userAgreedModLoaders
        if (currentDeps == null) {
            queued.userAgreedModLoaders = modpack.modLoaders
        }
        installMCAndModLoaders(state, currentDeps ?: modpack.modLoaders)
    }

    fun GameInstanceState.copyMods() {
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
                    linkDest.createLinkPointingTo(absolute)
                linkDest
            }
            .toSet()

        (existingEntries - linked).forEach { it.deleteIfExists() }
    }

    suspend fun GameInstanceState.prepareWithoutChangingInstalledMods(state: LaunchyState) {
        ensureDependenciesReady(state)
        copyMods()
    }

    @OptIn(ExperimentalPathApi::class)
    fun GameInstanceState.copyOverrides(state: LaunchyState) {
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

    suspend fun GameInstanceState.checkHashes(
        state: Map<ModID, DownloadInfo>
    ): List<Pair<ModID, DownloadInfo>> = coroutineScope {
        state.map { (modId, info) ->
            async(AppDispatchers.IOContext) {
                val check = runCatching { info.calculateSha1Hash(instance.minecraftDir) }.getOrNull()
                modId to info.copy(
                    hashCheck = when {
                        check == (info.desiredHash ?: check) -> HashCheck.VERIFIED
                        else -> HashCheck.FAILED
                    }
                )
            }
        }.awaitAll()
    }

    /**
     * Updates mod loader versions and mods to latest modpack definition.
     */
    suspend fun GameInstanceState.startInstall(
        state: LaunchyState,
        ignoreCachedCheck: Boolean = false
    ): Result<*> = coroutineScope {
        queued.userAgreedModLoaders = modpack.modLoaders
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
        val updatedHashes = checkHashes(queued.modDownloadInfo
            .filterValues { it.hashCheck == HashCheck.UNKNOWN || it.hashCheck == HashCheck.FAILED })


        updatedHashes.forEach { (modId, newInfo) ->
            queued.modDownloadInfo[modId] = newInfo
        }

        saveToConfig()

        if (queued.modDownloadInfo.any { it.value.hashCheck != HashCheck.VERIFIED }) {
            return@coroutineScope Result.failure(Exception("Failed to verify hashes"))
        }

        copyMods()

        saveToConfig()

        return@coroutineScope Result.success(Unit)
    }
}
