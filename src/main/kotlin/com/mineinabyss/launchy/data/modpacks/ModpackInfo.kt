package com.mineinabyss.launchy.data.modpacks

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.loadImageBitmap
import com.mineinabyss.launchy.data.Dirs
import com.mineinabyss.launchy.data.modpacks.source.PackSource
import com.mineinabyss.launchy.logic.Downloader
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
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
    private val backgroundImagePath = configDir / "background.png"

    @Transient
    private val logoPath = configDir / "logo.png"

    suspend fun getOrDownloadBackground(): ImageBitmap {
        if (!backgroundImagePath.exists()) Downloader.download(backgroundURL, backgroundImagePath)
        return loadImageBitmap(backgroundImagePath.inputStream())
    }

    suspend fun getOrDownloadLogo(): ImageBitmap {
        if (!logoPath.exists()) Downloader.download(logoURL, logoPath)
        return loadImageBitmap(logoPath.inputStream())
    }
}
