package com.mineinabyss.launchy.data.config

import com.charleskorn.kaml.decodeFromStream
import com.charleskorn.kaml.encodeToStream
import com.mineinabyss.launchy.data.Dirs
import com.mineinabyss.launchy.data.Formats
import com.mineinabyss.launchy.data.modpacks.ModpackInfo
import com.mineinabyss.launchy.state.LaunchyState
import kotlinx.serialization.Serializable
import java.nio.file.Path
import kotlin.io.path.*

class GameInstance(
    val instanceConfigDir: Path,
) {
    val config: GameInstanceConfig = GameInstanceConfig.read(instanceConfigDir / "instance.yml")
    val modpackInfo: ModpackInfo = ModpackInfo.read(instanceConfigDir / "packInfo.yml")

    companion object {
        fun create(state: LaunchyState, config: GameInstanceConfig) {
            val instanceDir = Dirs.modpackConfigDir(config.customName)
            instanceDir.createDirectories()

            Formats.yaml.encodeToStream(config, (instanceDir / "instance.yml").outputStream())
            state.gameInstances += GameInstance(instanceDir)
        }
        fun readAll(rootDir: Path): List<GameInstance> {
            return rootDir
                .listDirectoryEntries()
                .filter { it.isDirectory() }
                .mapNotNull {
                    runCatching { GameInstance(it) }
                        .onFailure { it.printStackTrace() }
                        .getOrNull()
                }
        }
    }
}
@Serializable
data class GameInstanceConfig(
    val customName: String,
    val cloudURL: String? = null,
    val isCloudInstance: Boolean = false,
) {
    companion object {
        fun read(file: Path): GameInstanceConfig {
            return Formats.yaml.decodeFromStream<GameInstanceConfig>(file.inputStream())
        }
    }
}
