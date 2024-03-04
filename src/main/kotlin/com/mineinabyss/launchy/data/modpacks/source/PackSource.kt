package com.mineinabyss.launchy.data.modpacks.source

import com.mineinabyss.launchy.data.modpacks.Modpack
import com.mineinabyss.launchy.data.modpacks.ModpackInfo
import com.mineinabyss.launchy.logic.Downloader
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.nio.file.Path
import kotlin.io.path.createFile
import kotlin.io.path.createParentDirectories
import kotlin.io.path.exists

@Serializable
sealed class PackSource {
    @Serializable
    @SerialName("localFile")
    class LocalFile(val type: PackType) : PackSource() {
        override suspend fun getOrDownloadLatestPack(packInfo: ModpackInfo, modpackDir: Path): Modpack? {
            val downloadTo = type.getFilePath(packInfo.configDir)
            val dir = packInfo.configDir
            val format = type.getFormat(downloadTo) ?: return null
            val mods = format.toGenericMods(modpackDir)
            val dependencies = format.getDependencies(dir)
            return Modpack(dependencies, mods, packInfo)
        }

    }

    @SerialName("singleFileUrl")
    @Serializable
    class SingleFileUrl(val url: String, val type: PackType) : PackSource() {
        override suspend fun getOrDownloadLatestPack(packInfo: ModpackInfo, modpackDir: Path): Modpack? {
            val downloadTo = type.getFilePath(modpackDir)
            if (!downloadTo.exists()) downloadTo.createParentDirectories().createFile()
            Downloader.download(url, downloadTo)
            return LocalFile(type).getOrDownloadLatestPack(packInfo, modpackDir)
        }
    }

    companion object {

    }

    abstract suspend fun getOrDownloadLatestPack(packInfo: ModpackInfo, modpackDir: Path): Modpack?
}
