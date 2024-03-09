package com.mineinabyss.launchy.data.modpacks.formats

import com.mineinabyss.launchy.data.GroupName
import com.mineinabyss.launchy.data.modpacks.*
import kotlinx.serialization.Serializable
import java.nio.file.Path

@Serializable
data class LaunchyPackFormat(
    val fabricVersion: String? = null,
    val minecraftVersion: String,
    val groups: Set<Group>,
    private val modGroups: Map<GroupName, Set<ModConfig>>,
) : PackFormat {
    override fun toGenericMods(downloadsDir: Path): Mods {
        return Mods(modGroups
            .mapKeys { (name, _) -> groups.single { it.name == name } }
            .mapValues { (_, mods) ->
                mods.map {
                    Mod(
                        downloadDir = downloadsDir,
                        info = it,
                        modId = it.id ?: it.name,
                        desiredHashes = null,
                    )
                }.toSet()
            })
    }

    override fun getModLoaders(): InstanceModLoaders {
        return InstanceModLoaders(minecraft = minecraftVersion, fabricLoader = fabricVersion)
    }

    override fun getOverridesPaths(configDir: Path): List<Path> = emptyList()
}
