package com.mineinabyss.launchy.data.config

import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import com.mineinabyss.launchy.data.Dirs
import com.mineinabyss.launchy.data.Formats
import com.mineinabyss.launchy.data.modpacks.source.PackSource
import com.mineinabyss.launchy.logic.Downloader
import com.mineinabyss.launchy.state.LaunchyState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.nio.file.Path
import kotlin.io.path.div
import kotlin.io.path.inputStream
import kotlin.time.Duration.Companion.seconds

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
    val backgroundPath = Dirs.imageCache / "background-${backgroundURL.hashCode().toHexString()}"

    @Transient
    val logoPath = Dirs.imageCache / "icon-${logoURL.hashCode().toHexString()}"

    @Transient
    private var cachedBackground: BitmapPainter? = null

    @Transient
    private var cachedLogo: BitmapPainter? = null

    suspend fun loadBackgroundImage(): BitmapPainter {
        cachedBackground?.let { return it }
        Downloader.download(backgroundURL, backgroundPath, override = false)
        val painter = BitmapPainter(loadImageBitmap(backgroundPath.inputStream()))
        cachedBackground = painter
        return painter
    }

    suspend fun loadLogo(): BitmapPainter {
        cachedLogo?.let { return it }
        Downloader.download(logoURL, logoPath, override = false)
        val painter = BitmapPainter(loadImageBitmap(logoPath.inputStream()))
        cachedLogo = painter
        return painter
    }

    @Composable
    fun produceBackgroundState(state: LaunchyState) = produceState(cachedBackground) {
        state.downloadContext.launch {
            value = loadBackgroundImage()
        }
    }

    @Composable
    fun produceLogoState(state: LaunchyState) = produceState(cachedLogo) {
        state.downloadContext.launch {
            value = loadLogo()
        }
    }

    companion object {
        fun read(path: Path) =
            Formats.yaml.decodeFromStream(serializer(), path.inputStream())
    }
}
