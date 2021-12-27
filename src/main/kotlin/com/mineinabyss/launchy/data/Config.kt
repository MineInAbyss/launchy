package com.mineinabyss.launchy.data

import com.charleskorn.kaml.Yaml
import com.mineinabyss.launchy.util.Option
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlin.io.path.inputStream
import kotlin.io.path.writeText


@Serializable
data class Config(
    val minecraftDir: String? = null,
    val groups: Map<GroupName, Option> = mutableMapOf(),
    val toggledMods: Set<ModName> = mutableSetOf(),
    val downloads: Map<ModName, DownloadURL> = mutableMapOf()
) {
    fun save() {
        Dirs.configFile.writeText(Yaml.default.encodeToString(this))
    }

    companion object {
        fun read() = runCatching {
            Yaml.default.decodeFromStream(serializer(), Dirs.configFile.inputStream())
        }.getOrDefault(Config())
    }
}
