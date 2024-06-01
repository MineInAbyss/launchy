package com.mineinabyss.launchy.instance.data

object ToggleMods {
    fun ModTogglesState.setModEnabled(mod: Mod, enabled: Boolean) {
        if (enabled) {
            enabledMods += mod
            enabledMods.filter { !mod.compatibleWith(it) }
                .forEach { setModEnabled(it, false) }
            disabledMods.filter { it.info.name in mod.info.requires }.forEach { setModEnabled(it, true) }
        } else {
            enabledMods -= mod
            // if a mod is disabled, disable all mods that depend on it
            enabledMods.filter { it.info.requires.contains(mod.info.name) }.forEach { setModEnabled(it, false) }
            // if a mod is disabled, and the dependency is only used by this mod, disable the dependency too, unless it's not marked as a dependency
            enabledMods.filter { dep ->
                mod.info.requires.contains(dep.info.name)  // if the mod depends on this dependency
                        && dep.info.dependency // if the dependency is marked as a dependency
                        && enabledMods.none { it.info.requires.contains(dep.info.name) }  // and no other mod depends on this dependency
//                        && !versions.modGroups.filterValues { it.contains(dep) }.keys.any { it.forceEnabled } // and the group the dependency is in is not force enabled
            }.forEach { setModEnabled(it, false) }
        }
        setModConfigEnabled(mod, enabled)
    }

    fun ModTogglesState.setModConfigEnabled(mod: Mod, enabled: Boolean) {
        if (mod.info.configUrl.isNotBlank() && enabled) enabledConfigs.add(mod)
        else enabledConfigs.remove(mod)
    }
}
