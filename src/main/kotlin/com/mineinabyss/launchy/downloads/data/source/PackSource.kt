package com.mineinabyss.launchy.downloads.data.source

import com.mineinabyss.launchy.config.data.GameInstance
import com.mineinabyss.launchy.core.data.Downloader
import com.mineinabyss.launchy.instance.data.Modpack
import com.mineinabyss.launchy.util.AppDispatchers
import com.mineinabyss.launchy.util.UpdateResult
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.io.path.notExists

@Serializable
sealed class PackSource {
    abstract suspend fun loadInstance(instance: GameInstance): Result<Modpack>

    abstract suspend fun updateInstance(instance: GameInstance): Result<GameInstance>

    @Serializable
    @SerialName("localFile")
    class LocalFile(val type: PackType) : PackSource() {
        override suspend fun loadInstance(instance: GameInstance): Result<Modpack> = runCatching {
            val format = type.getFormat(instance.configDir).getOrThrow()
            val mods = format.toGenericMods(instance.downloadsDir)
            val modLoaders = format.getModLoaders()
            Modpack(modLoaders, mods, format.getOverridesPaths(instance.configDir))
        }

        override suspend fun updateInstance(instance: GameInstance): Result<GameInstance> {
            return runCatching { GameInstance(instance.configDir) }
        }
    }

    @SerialName("downloadFromURL")
    @Serializable
    class DownloadFromURL(val url: String, val type: PackType) : PackSource() {
        override suspend fun loadInstance(instance: GameInstance): Result<Modpack> {
            val downloadTo = type.getFilePath(instance.configDir)
            if (downloadTo.notExists()) {
                Downloader.download(url, downloadTo, options = Downloader.Options(saveModifyHeadersFor = instance))
                type.afterDownload(instance.configDir)
            } else {
                AppDispatchers.IO.launch {
                    val result = Downloader.checkUpdates(instance, url)
                    if (result !is UpdateResult.UpToDate) instance.updatesAvailable = true
                }
            }
            return LocalFile(type).loadInstance(instance)
        }

        override suspend fun updateInstance(instance: GameInstance): Result<GameInstance> {
            return runCatching {
                val downloadTo = type.getFilePath(instance.configDir)
                Downloader.download(url, downloadTo, options = Downloader.Options(saveModifyHeadersFor = instance))
                type.afterDownload(instance.configDir)
                GameInstance(instance.configDir)
            }
        }
    }
}
