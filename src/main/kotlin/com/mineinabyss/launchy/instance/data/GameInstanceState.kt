package com.mineinabyss.launchy.instance.data

import com.mineinabyss.launchy.config.data.GameInstance
import com.mineinabyss.launchy.config.data.InstanceUserConfig
import com.mineinabyss.launchy.downloads.data.DownloadQueueState
import com.mineinabyss.launchy.downloads.data.DownloadState

class GameInstanceState(
    val instance: GameInstance,
    val modpack: Modpack,
    private val userConfig: InstanceUserConfig
) {
    val toggles: ModTogglesState = ModTogglesState(modpack, userConfig)
    val queued = DownloadQueueState(userConfig, modpack, toggles)
    val downloads = DownloadState()

    fun saveToConfig() {
        userConfig.copy(
            fullEnabledGroups = modpack.mods.modGroups
                .filter { toggles.enabledMods.containsAll(it.value) }.keys
                .map { it.name }.toSet(),
            userAgreedDeps = queued.userAgreedModLoaders,
            toggledMods = toggles.enabledMods.mapTo(mutableSetOf()) { it.modId },
            toggledConfigs = toggles.enabledConfigs.mapTo(mutableSetOf()) { it.modId } + toggles.enabledMods.filter { it.info.forceConfigDownload }
                .mapTo(mutableSetOf()) { it.info.name },
            seenGroups = modpack.mods.groups.map { it.name }.toSet(),
            modDownloadInfo = queued.modDownloadInfo,
//            configDownloadInfo = toggles.downloadConfigURLs.mapKeys { it.key.info.name },
        ).save(instance.userConfigFile)
    }
}
