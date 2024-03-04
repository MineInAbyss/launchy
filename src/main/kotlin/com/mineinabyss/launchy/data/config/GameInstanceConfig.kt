package com.mineinabyss.launchy.data.config

import com.charleskorn.kaml.decodeFromStream
import com.mineinabyss.launchy.data.Formats
import com.mineinabyss.launchy.data.modpacks.ModpackInfo
import kotlinx.serialization.Serializable
import java.nio.file.Path
import kotlin.io.path.*

class GameInstance(
    val instanceConfigDir: Path,
) {
    val config: GameInstanceConfig = GameInstanceConfig.read(instanceConfigDir / "instance.yml")
    val modpackInfo: ModpackInfo = ModpackInfo.read(instanceConfigDir / "packInfo.yml")

    companion object {
        fun readAll(rootDir: Path): List<GameInstance> {
            return rootDir
                .listDirectoryEntries()
                .filter { it.isDirectory() }
                .mapNotNull { runCatching { GameInstance(it) }.getOrNull() }
        }
    }
}
@Serializable
data class GameInstanceConfig(
    val customName: String? = null,
    val cloudURL: String? = null,
    val isCloudInstance: Boolean = false,
) {
    companion object {
        fun read(file: Path): GameInstanceConfig {
            return Formats.yaml.decodeFromStream<GameInstanceConfig>(file.inputStream())
        }
    }
}
