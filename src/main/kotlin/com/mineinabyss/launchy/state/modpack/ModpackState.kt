package com.mineinabyss.launchy.state.modpack

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.painter.BitmapPainter
import com.mineinabyss.launchy.data.config.ModpackUserConfig
import com.mineinabyss.launchy.data.modpacks.Modpack
import java.nio.file.Path
import kotlin.io.path.name

class ModpackState(
    val modpackDir: Path,
    val modpack: Modpack,
    private val userConfig: ModpackUserConfig
) {
    val packFolderName = modpackDir.name
    var background: BitmapPainter? by mutableStateOf(null)
    val toggles: ModTogglesState = ModTogglesState(modpack, userConfig)
    val queued = DownloadQueueState(modpack.mods, toggles)
    val downloads = DownloadState()

    fun saveToConfig() {
        userConfig.copy(
            fullEnabledGroups = modpack.mods.modGroups
                .filter { toggles.enabledMods.containsAll(it.value) }.keys
                .map { it.name }.toSet(),
            toggledMods = toggles.enabledMods.mapTo(mutableSetOf()) { it.info.name },
            toggledConfigs = toggles.enabledConfigs.mapTo(mutableSetOf()) { it.info.name } + toggles.enabledMods.filter { it.info.forceConfigDownload }
                .mapTo(mutableSetOf()) { it.info.name },
            seenGroups = modpack.mods.groups.map { it.name }.toSet(),
            modDownloads = toggles.downloadURLs.mapKeys { it.key.info.name },
            modConfigs = toggles.downloadConfigURLs.mapKeys { it.key.info.name },
        ).save(modpack.info.userConfigFile)
    }
}
