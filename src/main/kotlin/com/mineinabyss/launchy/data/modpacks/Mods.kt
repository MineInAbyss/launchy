package com.mineinabyss.launchy.data.modpacks

import com.mineinabyss.launchy.data.GroupName
import com.mineinabyss.launchy.data.ModName

data class Mods(
    val modGroups: Map<Group, Set<Mod>>,
) {
    val groups = modGroups.keys
    val mods = modGroups.values.flatten().toSet()

    private val nameToGroup: Map<GroupName, Group> = groups.associateBy { it.name }
    private val nameToMod: Map<ModName, Mod> = modGroups.values
        .flatten()
        .associateBy { it.info.name }

    //
    fun getMod(name: ModName): Mod? = nameToMod[name]
    fun getGroup(name: GroupName): Group? = nameToGroup[name]

    companion object {
        const val VERSIONS_URL = "https://raw.githubusercontent.com/MineInAbyss/launchy/master/versions.yml"

        fun withSingleGroup(mods: Collection<Mod>) = Mods(
            modGroups = mapOf(
                Group("Default", forceEnabled = true) to mods.toSet()
            )
        )
    }
}
