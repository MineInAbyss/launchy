package com.mineinabyss.launchy.data.modpacks.source

import com.mineinabyss.launchy.data.config.GameInstance
import com.mineinabyss.launchy.data.modpacks.Modpack
import com.mineinabyss.launchy.logic.Downloader
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class PackSource {
    @Serializable
    @SerialName("localFile")
    class LocalFile(val type: PackType) : PackSource() {
        override suspend fun loadInstance(instance: GameInstance): Result<Modpack> = runCatching {
            val format = type.getFormat(instance.configDir).getOrThrow()
            val mods = format.toGenericMods(instance.minecraftDir)
            val dependencies = format.getDependencies(instance.minecraftDir)
            Modpack(dependencies, mods, format.getOverridesPaths(instance.configDir))
        }
    }

    @SerialName("downloadFromURL")
    @Serializable
    class DownloadFromURL(val url: String, val type: PackType) : PackSource() {
        override suspend fun loadInstance(instance: GameInstance): Result<Modpack> {
            val downloadTo = type.getFilePath(instance.configDir)
            Downloader.download(url, downloadTo, onFinishDownloadWhenChanged = {
                type.afterDownload(instance.configDir)
            })
            return LocalFile(type).loadInstance(instance)
        }
    }

    abstract suspend fun loadInstance(instance: GameInstance): Result<Modpack>
}
