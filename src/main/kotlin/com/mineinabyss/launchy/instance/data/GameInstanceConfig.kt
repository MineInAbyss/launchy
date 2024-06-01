package com.mineinabyss.launchy.instance.data

import androidx.compose.runtime.Immutable
import com.charleskorn.kaml.decodeFromStream
import com.mineinabyss.launchy.downloads.data.source.PackSource
import com.mineinabyss.launchy.util.Dirs
import com.mineinabyss.launchy.util.Formats
import com.mineinabyss.launchy.util.urlToFileName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.nio.file.Path
import kotlin.io.path.div
import kotlin.io.path.inputStream

@Immutable
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
    @OptIn(ExperimentalCoroutinesApi::class)
    @Transient
    val downloadScope = CoroutineScope(Dispatchers.IO.limitedParallelism(1))

    @Transient
    val backgroundPath = Dirs.imageCache / "background-${urlToFileName(backgroundURL)}"

    @Transient
    val logoPath = Dirs.imageCache / "icon-${urlToFileName(logoURL)}"

//    fun saveTo(path: Path) = runCatching {
//        Formats.yaml.encodeToStream(this, path.outputStream())
//    }

    companion object {
        fun read(path: Path) = runCatching {
            Formats.yaml.decodeFromStream(serializer(), path.inputStream())
        }.onFailure { it.printStackTrace() }
    }
}
