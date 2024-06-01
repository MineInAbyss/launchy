package com.mineinabyss.launchy.config.data

import com.charleskorn.kaml.decodeFromStream
import com.mineinabyss.launchy.downloads.data.ModDownloader
import com.mineinabyss.launchy.instance.data.InstanceModLoaders
import com.mineinabyss.launchy.util.Formats
import com.mineinabyss.launchy.util.GroupName
import com.mineinabyss.launchy.util.ModID
import com.mineinabyss.launchy.util.hashing.Hashing.checksum
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString
import java.nio.file.Path
import java.security.MessageDigest
import kotlin.io.path.*

enum class HashCheck {
    UNKNOWN, VERIFIED, FAILED
}

@Serializable
data class DownloadInfo(
    val url: String,
    val path: String,
    val desiredHash: String?,
    val hashCheck: HashCheck,
    val result: ModDownloader.DownloadResult,
) {
    @Transient
    val systemPath = Path(path)

    fun failed(): Boolean {
        return result == ModDownloader.DownloadResult.Failed
                || systemPath.isRegularFile()
                || (desiredHash != null && hashCheck == HashCheck.FAILED)
    }

    fun calculateSha1Hash(minecraftDir: Path): String {
        val md = MessageDigest.getInstance("SHA-1")
        return (minecraftDir / systemPath).checksum(md)
    }
}

@Serializable
data class InstanceUserConfig(
    val userAgreedDeps: InstanceModLoaders? = null,
    val fullEnabledGroups: Set<GroupName> = setOf(),
    val fullDisabledGroups: Set<GroupName> = setOf(),
    val toggledMods: Set<ModID> = setOf(),
    val toggledConfigs: Set<ModID> = setOf(),
    val seenGroups: Set<GroupName> = setOf(),
    val modDownloadInfo: Map<ModID, DownloadInfo> = mapOf(),
//    val configDownloadInfo: Map<ModID, DownloadInfo> = mapOf(),
    val downloadUpdates: Boolean = true,
) {
    fun save(file: Path) {
        file.createParentDirectories().deleteIfExists()
        file.writeText(Formats.yaml.encodeToString<InstanceUserConfig>(this))
    }

    companion object {
        fun load(file: Path): Result<InstanceUserConfig> = runCatching {
            return@runCatching if (file.exists()) Formats.yaml.decodeFromStream<InstanceUserConfig>(file.inputStream())
            else InstanceUserConfig()
        }.onFailure { it.printStackTrace() }
    }
}
