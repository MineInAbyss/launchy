package com.mineinabyss.launchy.downloads.data

import androidx.compose.runtime.*
import com.mineinabyss.launchy.config.data.DownloadInfo
import com.mineinabyss.launchy.config.data.InstanceUserConfig
import com.mineinabyss.launchy.instance.data.ModTogglesState
import com.mineinabyss.launchy.instance.data.Modpack
import com.mineinabyss.launchy.util.ModID

class DownloadQueueState(
    private val userConfig: InstanceUserConfig,
    val modpack: Modpack,
    val toggles: ModTogglesState
) {
    /** Live mod download info, including mods that have been removed from the latest modpack version. */
    val modDownloadInfo = mutableStateMapOf<ModID, DownloadInfo>().apply {
        val availableIds = toggles.availableMods.map { it.modId }
        putAll(userConfig.modDownloadInfo.filter { it.key in availableIds })
    }

    /** Mods whose download url matches a previously downloaded url and exist on the filesystem */
    val failures by derivedStateOf {
        toggles.enabledMods.filter {
            modDownloadInfo[it.modId]?.failed() == true
        }
    }

    /** Toggled mods that haven't been previously installed (are new to the instance) */
    val newDownloads by derivedStateOf {
        toggles.enabledMods.filter { it.modId !in modDownloadInfo.keys }
    }

    /** Toggled mods that have previously been downloaded but whose URL has changed */
    val updates by derivedStateOf {
        toggles.enabledMods
            .filter { mod ->
                modDownloadInfo[mod.modId]?.let { mod.downloadUrl.toString() != it.url } == true
            }
    }

    /** Mods (currently listed in the Modpack) that were previously enabled, but no longer are */
    val deletions by derivedStateOf {
        (modpack.mods.mods - toggles.enabledMods).filter { modDownloadInfo.contains(it.modId) }
    }

    val areModLoaderUpdatesAvailable by derivedStateOf {
        modpack.modLoaders != userAgreedModLoaders
    }

    var userAgreedModLoaders by mutableStateOf(userConfig.userAgreedDeps)

    val needsInstall by derivedStateOf { updates + newDownloads + failures }

    val areUpdatesQueued by derivedStateOf { updates.isNotEmpty() }
    val areNewDownloadsQueued by derivedStateOf { newDownloads.isNotEmpty() }
    val areDeletionsQueued by derivedStateOf { deletions.isNotEmpty() }
    val areOperationsQueued by derivedStateOf {
        areUpdatesQueued || areNewDownloadsQueued || areDeletionsQueued || areModLoaderUpdatesAvailable
    }
}
