package com.mineinabyss.launchy.data.modpacks.types

import com.mineinabyss.launchy.data.GroupName
import com.mineinabyss.launchy.data.modpacks.*
import kotlinx.serialization.Serializable
import java.nio.file.Path

@Serializable
data class LaunchyPackFormat(
    val fabricVersion: String? = null,
    val minecraftVersion: String,
    val groups: Set<Group>,
    private val modGroups: Map<GroupName, Set<ModInfo>>,
) : PackFormat {
    override fun toGenericMods(minecraftDir: Path): Mods {
        return Mods(modGroups
            .mapKeys { (name, _) -> groups.single { it.name == name } }
            .mapValues { (_, mods) -> mods.map { Mod(minecraftDir, it) }.toSet() })
    }

    override fun getDependencies(minecraftDir: Path): PackDependencies {
        return PackDependencies(minecraft = minecraftVersion, fabricLoader = fabricVersion)
    }
}
