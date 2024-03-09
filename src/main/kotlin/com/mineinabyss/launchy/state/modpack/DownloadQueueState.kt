package com.mineinabyss.launchy.state.modpack

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import com.mineinabyss.launchy.data.ModID
import com.mineinabyss.launchy.data.config.DownloadInfo
import com.mineinabyss.launchy.data.config.ModpackUserConfig
import com.mineinabyss.launchy.data.modpacks.Modpack

class DownloadQueueState(
    private val userConfig: ModpackUserConfig,
    val modpack: Modpack,
    val toggles: ModTogglesState
) {
    /** Live mod download info, including mods that have been removed from the latest modpack version. */
    val modDownloadInfo = mutableStateMapOf<ModID, DownloadInfo>().apply {
        putAll(userConfig.modDownloadInfo)
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

    val needsInstall by derivedStateOf { updates + newDownloads + failures }

    val areUpdatesQueued by derivedStateOf { updates.isNotEmpty() }
    val areNewDownloadsQueued by derivedStateOf { newDownloads.isNotEmpty() }
    val areDeletionsQueued by derivedStateOf { deletions.isNotEmpty() }
    val areOperationsQueued by derivedStateOf {
        areUpdatesQueued || areNewDownloadsQueued || areDeletionsQueued
    }
}
