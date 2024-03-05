package com.mineinabyss.launchy.data.config

import com.mineinabyss.launchy.data.Formats
import com.mineinabyss.launchy.data.modpacks.source.PackSource
import kotlinx.serialization.Serializable
import java.nio.file.Path
import kotlin.io.path.inputStream

@Serializable
data class GameInstanceConfig(
    val name: String,
    val description: String,
    val backgroundURL: String,
    val logoURL: String,
    val source: PackSource,
    val hue: Float = 0f,
    val cloudInstanceURL: String? = null,
    val overrideMinecraftDir: String? = null,
) {

    companion object {
        fun read(path: Path) =
            Formats.yaml.decodeFromStream(serializer(), path.inputStream())
    }
}
