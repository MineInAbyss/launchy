package com.mineinabyss.launchy.instance.data

import androidx.compose.runtime.Immutable
import com.mineinabyss.launchy.util.GroupName
import com.mineinabyss.launchy.util.ModID

@Immutable
data class InstanceModList(
    val modGroups: Map<ModGroup, Set<Mod>>,
) {
    val groups = modGroups.keys
    val mods = modGroups.values.flatten().toSet()

    private val nameToGroup: Map<GroupName, ModGroup> = groups.associateBy { it.name }
    private val idToMod: Map<ModID, Mod> = modGroups.values
        .flatten()
        .associateBy { it.modId }

    //
    fun getModById(id: ModID): Mod? = idToMod[id]
    fun getGroup(name: GroupName): ModGroup? = nameToGroup[name]

//    companion object {
//        const val VERSIONS_URL = "https://raw.githubusercontent.com/MineInAbyss/launchy/master/versions.yml"
//
//        fun withSingleGroup(mods: Collection<Mod>) = InstanceModList(
//            modGroups = mapOf(
//                ModGroup("Default", forceEnabled = true) to mods.toSet()
//            )
//        )
//    }
}
