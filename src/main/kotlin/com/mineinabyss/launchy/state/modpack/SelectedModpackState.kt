package com.mineinabyss.launchy.state.modpack

import com.mineinabyss.launchy.data.Dirs
import com.mineinabyss.launchy.data.config.ModpackUserConfig
import com.mineinabyss.launchy.data.modpacks.Modpack
import kotlin.io.path.Path

class SelectedModpackState(
    modpackFolderName: String,
    val modpack: Modpack,
    private val userConfig: ModpackUserConfig
) {

    val modpackDir = userConfig.modpackMinecraftDir?.let { Path(it) } ?: Dirs.modpackDir(modpackFolderName)
    val modpackConfigDir = Dirs.modpackConfigDir(modpackFolderName)

    val toggles: ModTogglesState = ModTogglesState(modpack, userConfig)
    val queued = DownloadQueueState(modpack.mods, toggles)
    val downloads = DownloadState(queued)

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
        ).save(modpackConfigDir)
    }
}
