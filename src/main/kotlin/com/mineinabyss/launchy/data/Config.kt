package com.mineinabyss.launchy.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlin.io.path.inputStream
import kotlin.io.path.writeText

@Serializable
data class Config(
    val minecraftDir: String? = null,
    val fullEnabledGroups: Set<GroupName> = setOf(),
    val toggledMods: Set<ModName> = setOf(),
    val downloads: Map<ModName, DownloadURL> = mapOf(),
    val seenGroups: Set<GroupName> = setOf(),
    val installedFabricVersion: String? = null,
    val downloadUpdates: Boolean = true
) {
    fun save() {
        Dirs.configFile.writeText(Formats.yaml.encodeToString(this))
    }

    companion object {
        fun read() = runCatching {
            Formats.yaml.decodeFromStream(serializer(), Dirs.configFile.inputStream())
        }.getOrDefault(Config())
    }
}
