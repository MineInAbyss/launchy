package com.mineinabyss.launchy.instance.data.storage

import androidx.compose.runtime.Immutable
import com.mineinabyss.launchy.downloads.data.formats.SerializedPackFormat
import com.mineinabyss.launchy.downloads.data.sources.SerializedDownloadSource
import com.mineinabyss.launchy.util.Dirs
import com.mineinabyss.launchy.util.urlToFileName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.io.path.div

@Immutable
@Serializable
data class InstanceConfig(
    val name: String,
    val description: String,
    val backgroundURL: String,
    val logoURL: String,
    val source: SerializedDownloadSource,
    val pack: SerializedPackFormat,
    val hue: Float = 0f,
    val cloudInstanceURL: String? = null,
    val overrideMinecraftDir: String? = null,
) {

    @Transient
    val backgroundPath = Dirs.imageCache / "background-${urlToFileName(backgroundURL)}"

    @Transient
    val logoPath = Dirs.imageCache / "icon-${urlToFileName(logoURL)}"
}
