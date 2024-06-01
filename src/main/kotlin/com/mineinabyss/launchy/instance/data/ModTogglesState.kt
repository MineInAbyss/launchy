package com.mineinabyss.launchy.instance.data

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import com.mineinabyss.launchy.config.data.InstanceUserConfig
import com.mineinabyss.launchy.core.ui.mutableStateSetOf
import com.mineinabyss.launchy.instance.data.ToggleMods.setModEnabled

class ModTogglesState(
    val modpack: Modpack,
    val modpackConfig: InstanceUserConfig
) {
    val availableMods = mutableStateSetOf<Mod>().apply {
        addAll(modpack.mods.mods)
    }
    val enabledMods = mutableStateSetOf<Mod>().apply {
        addAll(modpackConfig.toggledMods.mapNotNull { modpack.mods.getModById(it) })
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
        addAll(modpackConfig.toggledConfigs.mapNotNull { modpack.mods.getModById(it) })
    }

    init {
        // trigger update incase we have dependencies
        enabledMods.forEach { setModEnabled(it, true) }
    }
}
