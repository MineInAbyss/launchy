package com.mineinabyss.launchy.instance.data.storage

import androidx.compose.runtime.Immutable
import com.mineinabyss.launchy.downloads.data.source.PackSource
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
    val source: PackSource,
    val hue: Float = 0f,
    val cloudInstanceURL: String? = null,
    val overrideMinecraftDir: String? = null,
) {

    @Transient
    val backgroundPath = Dirs.imageCache / "background-${urlToFileName(backgroundURL)}"

    @Transient
    val logoPath = Dirs.imageCache / "icon-${urlToFileName(logoURL)}"
}
