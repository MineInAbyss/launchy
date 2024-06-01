package com.mineinabyss.launchy.config.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.charleskorn.kaml.encodeToStream
import com.mineinabyss.launchy.core.data.Downloader
import com.mineinabyss.launchy.core.ui.LaunchyState
import com.mineinabyss.launchy.instance.data.GameInstanceState
import com.mineinabyss.launchy.util.*
import kotlinx.coroutines.launch
import java.nio.file.Path
import kotlin.io.path.*

class GameInstance(
    val configDir: Path,
) {
    val instanceFile = configDir / "instance.yml"
    val config: GameInstanceConfig = GameInstanceConfig.read(instanceFile).getOrThrow()

    val overridesDir = configDir / "overrides"

    val minecraftDir = config.overrideMinecraftDir?.let { Path(it) } ?: Dirs.modpackDir(configDir.name)

    val modsDir = (minecraftDir / "mods").createDirectories()
    val userMods = (minecraftDir / "modsFromUser").createDirectories()

    val downloadsDir: Path = minecraftDir / "launchyDownloads"
    val userConfigFile = (configDir / "config.yml")

    var updatesAvailable by mutableStateOf(false)
    var enabled: Boolean by mutableStateOf(true)

    suspend fun createModpackState(state: LaunchyState, awaitUpdatesCheck: Boolean = false): GameInstanceState? {
        val userConfig = InstanceUserConfig.load(userConfigFile).getOrNull() ?: InstanceUserConfig()

        val modpack = state.runTask("loadingModpack ${config.name}", InProgressTask("Loading modpack ${config.name}")) {
            config.source.loadInstance(this)
                .showDialogOnError("Failed to read instance")
                .getOrElse {
                    it.printStackTrace()
                    return null
                }
        }
        val cloudUrl = config.cloudInstanceURL
        if (cloudUrl != null) {
            AppDispatchers.IO.launch {
                val result = Downloader.checkUpdates(this@GameInstance, cloudUrl)
                if (result !is UpdateResult.UpToDate) updatesAvailable = true
            }.also { if(awaitUpdatesCheck) it.join() }
        }
        return GameInstanceState(this, modpack, userConfig)
    }

    init {
        require(configDir.isDirectory()) { "Game instance at $configDir must be a directory" }
        userMods
    }

    data class CloudInstanceWithHeaders(
        val config: GameInstanceConfig,
        val url: String,
        val headers: Downloader.ModifyHeaders,
    )

    companion object {
        fun createCloudInstance(state: LaunchyState, cloud: CloudInstanceWithHeaders) {
            val instanceDir = Dirs.modpackConfigDir(cloud.config.name)
            instanceDir.createDirectories()

            Formats.yaml.encodeToStream(
                cloud.config.copy(cloudInstanceURL = cloud.url),
                (instanceDir / "instance.yml").outputStream()
            )
            val instance = GameInstance(instanceDir)
            Downloader.saveHeaders(instance, cloud.url, cloud.headers)
            state.gameInstances += instance
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
