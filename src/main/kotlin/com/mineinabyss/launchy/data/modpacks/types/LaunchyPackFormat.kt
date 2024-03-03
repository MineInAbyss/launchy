package com.mineinabyss.launchy.data.modpacks.types

import com.mineinabyss.launchy.data.GroupName
import com.mineinabyss.launchy.data.modpacks.*
import kotlinx.serialization.Serializable
import java.nio.file.Path

@Serializable
data class LaunchyPackFormat(
    val fabricVersion: String,
    val minecraftVersion: String,
    val groups: Set<Group>,
    private val modGroups: Map<GroupName, Set<ModInfo>>,
) : PackFormat {
    override fun toGenericMods(packDir: Path): Mods {
        return Mods(modGroups
            .mapKeys { (name, _) -> groups.single { it.name == name } }
            .mapValues { (_, mods) -> mods.map { Mod(packDir, it) }.toSet() })
    }

    override fun getDependencies(packDir: Path): PackDependencies {
        return PackDependencies(minecraft = minecraftVersion, fabricLoader = fabricVersion)
    }
}
