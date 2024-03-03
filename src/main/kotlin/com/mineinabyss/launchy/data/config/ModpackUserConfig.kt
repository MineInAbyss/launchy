package com.mineinabyss.launchy.data.config

import com.charleskorn.kaml.decodeFromStream
import com.mineinabyss.launchy.data.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import java.nio.file.Path
import kotlin.io.path.*

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
    fun save(file: Path) {
        file.createParentDirectories().deleteIfExists()
        file.writeText(Formats.yaml.encodeToString<ModpackUserConfig>(this))
    }

    companion object {
        fun load(packConfigDir: Path): ModpackUserConfig {
            val file = packConfigDir / "config.yml"
            return if (file.exists()) Formats.yaml.decodeFromStream<ModpackUserConfig>(file.inputStream())
            else ModpackUserConfig()
        }
    }
}
