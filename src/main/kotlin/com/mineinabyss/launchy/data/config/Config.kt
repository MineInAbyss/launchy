package com.mineinabyss.launchy.data.config

import com.charleskorn.kaml.decodeFromStream
import com.mineinabyss.launchy.data.Dirs
import com.mineinabyss.launchy.data.Formats
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlin.io.path.inputStream
import kotlin.io.path.writeText


@Serializable
data class Config(
    val handledImportOptions: Boolean = false,
    val onboardingComplete: Boolean = false,
    val currentProfile: PlayerProfile? = null,
    val javaPath: String? = null,
    val jvmArguments: String? = null,
    val memoryAllocation: Int? = null,
    val useRecommendedJvmArguments: Boolean = true,
    val preferHue: Float? = null,
    val startInFullscreen: Boolean = false,
    val lastPlayedMap: Map<String, Long> = mapOf(),
) {
    fun save() {
        Dirs.configFile.writeText(Formats.yaml.encodeToString(this))
    }

    companion object {
        fun read(): Result<Config> = runCatching {
            Formats.yaml.decodeFromStream(serializer(), Dirs.configFile.inputStream())
        }.onFailure { it.printStackTrace() }
    }
}
