package com.mineinabyss.launchy.instance.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import com.charleskorn.kaml.encodeToStream
import com.mineinabyss.launchy.core.ui.LaunchyState
import com.mineinabyss.launchy.downloads.data.Downloader
import com.mineinabyss.launchy.instance.ui.GameInstanceState
import com.mineinabyss.launchy.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.file.Path
import kotlin.io.path.*

class GameInstanceDataSource(
    val configDir: Path,
    val config: GameInstanceConfig,
) {
    val instanceFile = configDir / "instance.yml"
    val overridesDir = configDir / "overrides"
    val imageLoaderDispatcher = Dispatchers.IO.limitedParallelism(1)

    val minecraftDir = config.overrideMinecraftDir?.let { Path(it) } ?: Dirs.modpackDir(configDir.name)

    val modsDir = (minecraftDir / "mods").createDirectories()
    val userMods = (minecraftDir / "modsFromUser").createDirectories()

    val downloadsDir: Path = minecraftDir / "launchyDownloads"
    val userConfigFile = (configDir / "config.yml")

    var updatesAvailable by mutableStateOf(false)
    var enabled: Boolean by mutableStateOf(true)

    suspend fun loadModList(): InstanceModList {
        TODO()
    }
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
                val result = Downloader.checkUpdates(this@GameInstanceDataSource, cloudUrl)
                if (result !is UpdateResult.UpToDate) updatesAvailable = true
            }.also { if (awaitUpdatesCheck) it.join() }
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
            val instance = GameInstanceDataSource(instanceDir)
            Downloader.saveHeaders(instance, cloud.url, cloud.headers)
            state.gameInstances += instance
        }
    }

    private suspend fun loadBackground() {
        runCatching {
            Downloader.download(config.backgroundURL, config.backgroundPath, Downloader.Options(overwrite = false))
            val painter = BitmapPainter(loadImageBitmap(config.backgroundPath.inputStream()))
            cachedBackground = painter
        }.onFailure { it.printStackTrace() }
    }

    private suspend fun loadLogo() {
        runCatching {
            Downloader.download(config.logoURL, config.logoPath, Downloader.Options(overwrite = false))
            val painter = BitmapPainter(loadImageBitmap(config.logoPath.inputStream()))
            cachedLogo = painter
        }.onFailure { it.printStackTrace() }
    }

    private var cachedBackground: BitmapPainter? = null
    private var cachedLogo: BitmapPainter? = null

    suspend fun getBackground() = withContext(imageLoaderDispatcher) {
        if (cachedBackground == null) loadLogo()
        cachedBackground
    }

    suspend fun getLogo(): BitmapPainter? = withContext(imageLoaderDispatcher) {
        if (cachedLogo == null) loadLogo()
        cachedLogo
    }
}
