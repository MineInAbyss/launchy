package com.mineinabyss.launchy.state.modpack

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.mineinabyss.launchy.data.config.GameInstance
import com.mineinabyss.launchy.data.config.InstanceUserConfig
import com.mineinabyss.launchy.data.modpacks.Modpack

class GameInstanceState(
    val instance: GameInstance,
    val modpack: Modpack,
    private val userConfig: InstanceUserConfig
) {
    val toggles: ModTogglesState = ModTogglesState(modpack, userConfig)
    val queued = DownloadQueueState(userConfig, modpack, toggles)
    val downloads = DownloadState()
    var userAgreedModLoaders by mutableStateOf(userConfig.userAgreedDeps)

    fun saveToConfig() {
        userConfig.copy(
            fullEnabledGroups = modpack.mods.modGroups
                .filter { toggles.enabledMods.containsAll(it.value) }.keys
                .map { it.name }.toSet(),
            userAgreedDeps = userAgreedModLoaders,
            toggledMods = toggles.enabledMods.mapTo(mutableSetOf()) { it.info.name },
            toggledConfigs = toggles.enabledConfigs.mapTo(mutableSetOf()) { it.info.name } + toggles.enabledMods.filter { it.info.forceConfigDownload }
                .mapTo(mutableSetOf()) { it.info.name },
            seenGroups = modpack.mods.groups.map { it.name }.toSet(),
            modDownloadInfo = queued.modDownloadInfo,
//            configDownloadInfo = toggles.downloadConfigURLs.mapKeys { it.key.info.name },
        ).save(instance.userConfigFile)
    }
}
