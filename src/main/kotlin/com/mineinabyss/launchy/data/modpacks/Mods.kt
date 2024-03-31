package com.mineinabyss.launchy.data.modpacks

import com.mineinabyss.launchy.data.GroupName
import com.mineinabyss.launchy.data.ModID

data class Mods(
    val modGroups: Map<Group, Set<Mod>>,
) {
    val groups = modGroups.keys
    val mods = modGroups.values.flatten().toSet()

    private val nameToGroup: Map<GroupName, Group> = groups.associateBy { it.name }
    private val idToMod: Map<ModID, Mod> = modGroups.values
        .flatten()
        .associateBy { it.modId }

    //
    fun getModById(id: ModID): Mod? = idToMod[id]
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
