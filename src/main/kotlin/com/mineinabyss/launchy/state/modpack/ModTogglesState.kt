package com.mineinabyss.launchy.state.modpack

import androidx.compose.runtime.*
import com.mineinabyss.launchy.data.ConfigURL
import com.mineinabyss.launchy.data.DownloadURL
import com.mineinabyss.launchy.data.config.ModpackUserConfig
import com.mineinabyss.launchy.data.modpacks.Mod
import com.mineinabyss.launchy.data.modpacks.Modpack
import com.mineinabyss.launchy.logic.ToggleMods.setModEnabled
import com.mineinabyss.launchy.state.mutableStateSetOf

class ModTogglesState(
    val modpack: Modpack,
    val modpackConfig: ModpackUserConfig
) {
    val downloadURLs = mutableStateMapOf<Mod, DownloadURL>().apply {
        putAll(modpackConfig.modDownloads
            .mapNotNull { modpack.mods.getMod(it.key)?.to(it.value) }
            .toMap()
        )
    }

    val downloadConfigURLs = mutableStateMapOf<Mod, ConfigURL>().apply {
        putAll(modpackConfig.modConfigs
            .mapNotNull { modpack.mods.getMod(it.key)?.to(it.value) }
            .toMap()
        )
    }

    var nonDownloadedMods by mutableStateOf(setOf<Mod>())
        private set


    val enabledMods = mutableStateSetOf<Mod>().apply {
        addAll(modpackConfig.toggledMods.mapNotNull { modpack.mods.getMod(it) })
        val defaultEnabled = modpack.mods.groups
            .filter { it.enabledByDefault }
            .map { it.name } - modpackConfig.seenGroups
        val fullEnabled = modpackConfig.fullEnabledGroups
        val forceEnabled = modpack.mods.groups.filter { it.forceEnabled }.map { it.name }
        val forceDisabled = modpack.mods.groups.filter { it.forceDisabled }
        val fullDisabled = modpackConfig.fullDisabledGroups
        addAll(((fullEnabled + defaultEnabled + forceEnabled).toSet())
            .mapNotNull { modpack.mods.getGroup(it) }
            .mapNotNull { modpack.mods.modGroups[it] }.flatten()
        )
        removeAll((forceDisabled + fullDisabled).toSet().mapNotNull { modpack.mods.modGroups[it] }.flatten().toSet())
    }

    val disabledMods: Set<Mod> by derivedStateOf { modpack.mods.mods - enabledMods }

    val enabledModsWithConfig by derivedStateOf {
        enabledMods.filter { it.info.configUrl != "" }
    }

    val enabledConfigs: MutableSet<Mod> = mutableStateSetOf<Mod>().apply {
        addAll(modpackConfig.toggledConfigs.mapNotNull { modpack.mods.getMod(it) })
    }


    val upToDateMods by derivedStateOf {
        enabledMods.filter { it in downloadURLs && downloadURLs[it] == it.info.url && it !in nonDownloadedMods }
    }

    val upToDateConfigs by derivedStateOf {
        enabledMods.filter { it in downloadConfigURLs && downloadConfigURLs[it] == it.info.configUrl }
    }

    fun checkNonDownloadedMods() {
        downloadURLs.filter { !it.key.isDownloaded }.keys.also { nonDownloadedMods = it }
    }

    init {
        // trigger update incase we have dependencies
        enabledMods.forEach { setModEnabled(it, true) }
        checkNonDownloadedMods()
    }
}
