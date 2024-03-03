package com.mineinabyss.launchy.data.modpacks

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.loadImageBitmap
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.data.Dirs
import com.mineinabyss.launchy.data.config.ModpackUserConfig
import com.mineinabyss.launchy.data.modpacks.source.PackSource
import com.mineinabyss.launchy.logic.Downloader
import com.mineinabyss.launchy.state.LaunchyState
import com.mineinabyss.launchy.state.modpack.ModpackState
import com.mineinabyss.launchy.ui.screens.Dialog
import com.mineinabyss.launchy.ui.screens.dialog
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
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

    suspend fun createModpackState(): ModpackState? {
        val userConfig = ModpackUserConfig()
        val modpackDir = userConfig.modpackMinecraftDir?.let { Path(it) } ?: Dirs.modpackDir(folderName)
        val modpack = source.getOrDownloadLatestPack(this, modpackDir) ?: run {
            dialog = Dialog.Error("Failed to download modpack", "")
            return null
        }
        return ModpackState(modpackDir, modpack, userConfig).apply {
            this.background = background
        }
    }
}
