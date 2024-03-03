package com.mineinabyss.launchy.data.modpacks.source

import com.mineinabyss.launchy.data.modpacks.Modpack
import com.mineinabyss.launchy.data.modpacks.ModpackInfo
import com.mineinabyss.launchy.logic.Downloader
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.nio.file.Path
import kotlin.io.path.createFile
import kotlin.io.path.createParentDirectories
import kotlin.io.path.div
import kotlin.io.path.exists

@Serializable
sealed class PackSource {
    @SerialName("singleFileUrl")
    @Serializable
    class SingleFileUrl(val url: String, val type: PackType) : PackSource() {
        override suspend fun getOrDownloadLatestPack(packInfo: ModpackInfo, modpackDir: Path): Modpack? {
            val dir = packInfo.configDir
            val ext = when (type) {
                PackType.Launchy -> "yml"
                PackType.Modrinth -> "zip"
            }
            val downloadTo = dir / "pack.$ext"
            if (!downloadTo.exists()) downloadTo.createParentDirectories().createFile()
            Downloader.download(url, downloadTo)
            val format = type.getFormat(downloadTo) ?: return null
            val mods = format.toGenericMods(modpackDir)
            val dependencies = format.getDependencies(dir)
            return Modpack(dependencies, mods, packInfo)
        }
    }

    abstract suspend fun getOrDownloadLatestPack(packInfo: ModpackInfo, modpackDir: Path): Modpack?
}
