package com.mineinabyss.launchy.state.modpack

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.mineinabyss.launchy.data.modpacks.Mods

class DownloadQueueState(
    val mods: Mods,
    val toggles: ModTogglesState
) {
    val downloads by derivedStateOf { (toggles.enabledMods - toggles.upToDateMods.toSet()) + (toggles.enabledModsWithConfig - toggles.upToDateConfigs.toSet()) }
    val updates by derivedStateOf { downloads.filter { it.isDownloaded }.toSet() }
    val installs by derivedStateOf { downloads - updates }
    val deletions by derivedStateOf {
        deleted // Depend on state
        toggles.disabledMods
            .filter { it.isDownloaded }
            .also { if (it.isEmpty()) toggles.checkNonDownloadedMods() }
    }

    val areUpdatesQueued by derivedStateOf { updates.isNotEmpty() }
    val areInstallsQueued by derivedStateOf { installs.isNotEmpty() }
    val areDeletionsQueued by derivedStateOf { deletions.isNotEmpty() }
    val areOperationsQueued by derivedStateOf { areUpdatesQueued || areInstallsQueued || areDeletionsQueued }

    internal var deleted by mutableStateOf(0)
}
