package com.mineinabyss.launchy.data.modpacks

import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import com.charleskorn.kaml.decodeFromStream
import com.mineinabyss.launchy.data.Dirs
import com.mineinabyss.launchy.data.Formats
import com.mineinabyss.launchy.data.config.ModpackUserConfig
import com.mineinabyss.launchy.data.modpacks.source.PackSource
import com.mineinabyss.launchy.logic.Downloader
import com.mineinabyss.launchy.state.modpack.ModpackState
import com.mineinabyss.launchy.ui.screens.Dialog
import com.mineinabyss.launchy.ui.screens.dialog
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.inputStream

@Serializable
class ModpackInfo(
    val folderName: String,
    val name: String,
    val desc: String,
    val backgroundURL: String,
    val logoURL: String,
    val source: PackSource,
    val hue: Float = 0f,
) {
    @Transient
    val configDir = Dirs.modpackConfigDir(folderName)

    @Transient
    val userConfigFile = (configDir / "config.yml")

    @Transient
    private val backgroundImagePath = configDir / "background.png"

    @Transient
    private val logoPath = configDir / "logo.png"

    @Transient
    private var cachedBackground: BitmapPainter? = null

    @Transient
    private var cachedLogo: BitmapPainter? = null

    suspend fun getOrDownloadBackground(): BitmapPainter {
        cachedBackground?.let { return it }
        if (!backgroundImagePath.exists()) Downloader.download(backgroundURL, backgroundImagePath)
        val painter =
            BitmapPainter(loadImageBitmap(backgroundImagePath.inputStream()), filterQuality = FilterQuality.High)
        cachedBackground = painter
        return painter
    }

    suspend fun getOrDownloadLogo(): BitmapPainter {
        cachedLogo?.let { return it }
        if (!logoPath.exists()) Downloader.download(logoURL, logoPath)
        val painter = BitmapPainter(loadImageBitmap(logoPath.inputStream()), filterQuality = FilterQuality.High)
        cachedLogo = painter
        return painter
    }

    suspend fun createModpackState(): ModpackState? {
        val userConfig =
            if (userConfigFile.exists()) Formats.yaml.decodeFromStream<ModpackUserConfig>(userConfigFile.inputStream())
            else ModpackUserConfig()
        val modpackDir = userConfig.modpackMinecraftDir?.let { Path(it) } ?: Dirs.modpackDir(folderName)
        val modpack = source.getOrDownloadLatestPack(this, modpackDir) ?: run {
            dialog = Dialog.Error("Failed to download modpack", "")
            return null
        }
        return ModpackState(modpackDir, modpack, userConfig)
    }

    companion object {
        fun read(path: Path) =
            Formats.yaml.decodeFromStream(serializer(), path.inputStream())
    }
}
