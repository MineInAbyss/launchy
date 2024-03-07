package com.mineinabyss.launchy.data.config

import com.charleskorn.kaml.encodeToStream
import com.mineinabyss.launchy.data.Dirs
import com.mineinabyss.launchy.data.Formats
import com.mineinabyss.launchy.state.LaunchyState
import com.mineinabyss.launchy.state.modpack.ModpackState
import com.mineinabyss.launchy.ui.screens.Dialog
import com.mineinabyss.launchy.ui.screens.dialog
import java.nio.file.Path
import kotlin.io.path.*

class GameInstance(
    val configDir: Path,
) {
    val overridesDir = configDir / "overrides"

    init {
        require(configDir.isDirectory()) { "Game instance at $configDir must be a directory" }
    }

    val config: GameInstanceConfig = GameInstanceConfig.read(configDir / "instance.yml").getOrThrow()

    val minecraftDir = config.overrideMinecraftDir?.let { Path(it) } ?: Dirs.modpackDir(configDir.name)

    val userConfigFile = (configDir / "config.yml")

    suspend fun createModpackState(): ModpackState? {
        val userConfig = ModpackUserConfig.load(userConfigFile).getOrNull() ?: ModpackUserConfig()
        val modpack = config.source.loadInstance(this)
            .getOrElse {
                dialog = Dialog.Error(
                    "Failed read instance", it
                        .stackTraceToString()
                        .split("\n").take(5).joinToString("\n")
                )
                it.printStackTrace()
                return null
            }
        return ModpackState(this, modpack, userConfig)
    }

    companion object {
        fun create(state: LaunchyState, config: GameInstanceConfig) {
            val instanceDir = Dirs.modpackConfigDir(config.name)
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
