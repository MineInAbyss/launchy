package com.mineinabyss.launchy.data.config

import com.mineinabyss.launchy.data.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import java.nio.file.Path
import kotlin.io.path.createParentDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.div
import kotlin.io.path.writeText

@Serializable
data class ModpackUserConfig(
    val modpackMinecraftDir: String? = null,
    val fullEnabledGroups: Set<GroupName> = setOf(),
    val fullDisabledGroups: Set<GroupName> = setOf(),
    val toggledMods: Set<ModName> = setOf(),
    val toggledConfigs: Set<ModName> = setOf(),
    val seenGroups: Set<GroupName> = setOf(),
    val modDownloads: Map<ModName, DownloadURL> = mapOf(),
    val modConfigs: Map<ModName, ConfigURL> = mapOf(),
    val downloadUpdates: Boolean = true,
) {
    fun save(packConfigDir: Path) {
        val file = (packConfigDir / "config.yml").createParentDirectories()
        file.deleteIfExists()
        file.writeText(Formats.yaml.encodeToString(this))
    }
}
