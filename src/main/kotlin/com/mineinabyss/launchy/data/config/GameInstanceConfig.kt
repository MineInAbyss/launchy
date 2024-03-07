package com.mineinabyss.launchy.data.config

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import com.mineinabyss.launchy.data.Dirs
import com.mineinabyss.launchy.data.Formats
import com.mineinabyss.launchy.data.modpacks.source.PackSource
import com.mineinabyss.launchy.logic.Downloader
import com.mineinabyss.launchy.state.LaunchyState
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.nio.file.Path
import kotlin.io.path.div
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
    val backgroundPath = Dirs.imageCache / "background-${backgroundURL.hashCode().toHexString()}"

    @Transient
    val logoPath = Dirs.imageCache / "icon-${logoURL.hashCode().toHexString()}"

    @Transient
    private var cachedBackground = mutableStateOf<BitmapPainter?>(null)

    @Transient
    private var cachedLogo = mutableStateOf<BitmapPainter?>(null)


    private suspend fun loadBackground() {
        if (cachedBackground.value != null) return
        runCatching {
            Downloader.download(backgroundURL, backgroundPath, override = false)
            val painter = BitmapPainter(loadImageBitmap(backgroundPath.inputStream()))
            cachedBackground.value = painter
        }.onFailure { it.printStackTrace() }
    }

    private suspend fun loadLogo() {
        if (cachedLogo.value != null) return
        runCatching {
            Downloader.download(logoURL, logoPath, override = false)
            val painter = BitmapPainter(loadImageBitmap(logoPath.inputStream()))
            cachedLogo.value = painter
        }.onFailure { it.printStackTrace() }
    }

    @Composable
    fun getBackground(state: LaunchyState) = remember {
        cachedBackground.also {
            if (it.value == null) state.ioScope.launch { loadBackground() }
        }
    }

    @Composable
    fun getLogo(state: LaunchyState) = remember {
        cachedLogo.also {
            if (it.value == null) state.ioScope.launch { loadLogo() }
        }
    }

    companion object {
        fun read(path: Path) = runCatching {
            Formats.yaml.decodeFromStream(serializer(), path.inputStream())
        }.onFailure { it.printStackTrace() }
    }
}
