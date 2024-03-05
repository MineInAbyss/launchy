package com.mineinabyss.launchy.data.config

import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import com.mineinabyss.launchy.data.Dirs
import com.mineinabyss.launchy.data.Formats
import com.mineinabyss.launchy.data.modpacks.source.PackSource
import com.mineinabyss.launchy.logic.Downloader
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.nio.file.Path
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.inputStream

@Serializable
@OptIn(ExperimentalStdlibApi::class)
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
    @Transient
    private val backgroundImagePath = Dirs.tmp / "background-${backgroundURL.hashCode().toHexString()}.png"

    @Transient
    private val logoPath = Dirs.tmp / "icon-${backgroundURL.hashCode().toHexString()}.png"

    @Transient
    private var cachedBackground: BitmapPainter? = null

    @Transient
    private var cachedLogo: BitmapPainter? = null

    suspend fun loadBackgroundFromTmpFile(): BitmapPainter {
        cachedBackground?.let { return it }
        if (!backgroundImagePath.exists()) Downloader.download(backgroundURL, backgroundImagePath)
        val painter =
            BitmapPainter(loadImageBitmap(backgroundImagePath.inputStream()), filterQuality = FilterQuality.High)
        cachedBackground = painter
        return painter
    }

    suspend fun loadIconFromTmpFile(): BitmapPainter {
        cachedLogo?.let { return it }
        if (!logoPath.exists()) Downloader.download(logoURL, logoPath)
        val painter = BitmapPainter(loadImageBitmap(logoPath.inputStream()), filterQuality = FilterQuality.High)
        cachedLogo = painter
        return painter
    }

    companion object {
        fun read(path: Path) =
            Formats.yaml.decodeFromStream(serializer(), path.inputStream())
    }
}
