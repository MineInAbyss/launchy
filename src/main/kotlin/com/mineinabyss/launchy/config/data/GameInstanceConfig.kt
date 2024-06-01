package com.mineinabyss.launchy.config.data

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import com.charleskorn.kaml.decodeFromStream
import com.charleskorn.kaml.encodeToStream
import com.mineinabyss.launchy.core.data.Downloader
import com.mineinabyss.launchy.downloads.data.source.PackSource
import com.mineinabyss.launchy.util.Dirs
import com.mineinabyss.launchy.util.Formats
import com.mineinabyss.launchy.util.urlToFileName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.nio.file.Path
import kotlin.io.path.div
import kotlin.io.path.inputStream
import kotlin.io.path.outputStream

@Serializable
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
    val backgroundPath = Dirs.imageCache / "background-${urlToFileName(backgroundURL)}"

    @Transient
    val logoPath = Dirs.imageCache / "icon-${urlToFileName(logoURL)}"

    @Transient
    private var cachedBackground = mutableStateOf<BitmapPainter?>(null)

    @Transient
    private var cachedLogo = mutableStateOf<BitmapPainter?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Transient
    val downloadScope = CoroutineScope(Dispatchers.IO.limitedParallelism(1))

    private suspend fun loadBackground() {
        runCatching {
            Downloader.download(backgroundURL, backgroundPath, Downloader.Options(overwrite = false))
            val painter = BitmapPainter(loadImageBitmap(backgroundPath.inputStream()))
            cachedBackground.value = painter
        }.onFailure { it.printStackTrace() }
    }

    private suspend fun loadLogo() {
        runCatching {
            Downloader.download(logoURL, logoPath, Downloader.Options(overwrite = false))
            val painter = BitmapPainter(loadImageBitmap(logoPath.inputStream()))
            cachedLogo.value = painter
        }.onFailure { it.printStackTrace() }
    }

    fun getBackgroundAsState() =
        cachedBackground.also {
            if (it.value == null) downloadScope.launch { loadBackground() }
        }

    fun getLogoAsState() =
        cachedLogo.also {
            if (it.value == null) downloadScope.launch { loadLogo() }
        }

    fun saveTo(path: Path) = runCatching {
        Formats.yaml.encodeToStream(this, path.outputStream())
    }

    companion object {
        fun read(path: Path) = runCatching {
            Formats.yaml.decodeFromStream(serializer(), path.inputStream())
        }.onFailure { it.printStackTrace() }
    }
}
