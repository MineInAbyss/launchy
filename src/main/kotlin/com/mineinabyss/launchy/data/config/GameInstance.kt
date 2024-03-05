package com.mineinabyss.launchy.data.config

import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import com.charleskorn.kaml.decodeFromStream
import com.charleskorn.kaml.encodeToStream
import com.mineinabyss.launchy.data.Dirs
import com.mineinabyss.launchy.data.Formats
import com.mineinabyss.launchy.logic.Downloader
import com.mineinabyss.launchy.state.LaunchyState
import com.mineinabyss.launchy.state.modpack.ModpackState
import com.mineinabyss.launchy.ui.screens.Dialog
import com.mineinabyss.launchy.ui.screens.dialog
import java.nio.file.Path
import kotlin.io.path.*

class GameInstance(
    val configDir: Path,
) {
    init {
        require(configDir.isDirectory()) { "Game instance at $configDir must be a directory" }
    }

    val config: GameInstanceConfig = GameInstanceConfig.read(configDir / "instance.yml")

    val minecraftDir = config.overrideMinecraftDir?.let { Path(it) } ?: Dirs.modpackDir(configDir.name)

    val userConfigFile = (configDir / "config.yml")
    private val backgroundImagePath = configDir / "background.png"
    private val logoPath = configDir / "logo.png"
    private var cachedBackground: BitmapPainter? = null
    private var cachedLogo: BitmapPainter? = null

    suspend fun getOrDownloadBackground(): BitmapPainter {
        cachedBackground?.let { return it }
        if (!backgroundImagePath.exists()) Downloader.download(config.backgroundURL, backgroundImagePath)
        val painter =
            BitmapPainter(loadImageBitmap(backgroundImagePath.inputStream()), filterQuality = FilterQuality.High)
        cachedBackground = painter
        return painter
    }

    suspend fun getOrDownloadLogo(): BitmapPainter {
        cachedLogo?.let { return it }
        if (!logoPath.exists()) Downloader.download(config.logoURL, logoPath)
        val painter = BitmapPainter(loadImageBitmap(logoPath.inputStream()), filterQuality = FilterQuality.High)
        cachedLogo = painter
        return painter
    }

    suspend fun createModpackState(): ModpackState? {
        val userConfig =
            if (userConfigFile.exists()) Formats.yaml.decodeFromStream<ModpackUserConfig>(userConfigFile.inputStream())
            else ModpackUserConfig()
        val modpack = config.source.loadInstance(this)
            .getOrElse {
                dialog = Dialog.Error("Failed read instance", it.message ?: "Unknown error")
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
