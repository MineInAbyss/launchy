package com.mineinabyss.launchy.instance.data.storage

import com.charleskorn.kaml.decodeFromStream
import com.mineinabyss.launchy.instance.data.DownloadInfo
import com.mineinabyss.launchy.instance.data.ModLoaderModel
import com.mineinabyss.launchy.util.Formats
import com.mineinabyss.launchy.util.GroupName
import com.mineinabyss.launchy.util.ModID
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import java.nio.file.Path
import kotlin.io.path.*

@Serializable
data class InstanceUserConfig(
    val userAgreedDeps: ModLoaderModel? = null,
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
