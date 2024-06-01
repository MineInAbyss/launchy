package com.mineinabyss.launchy.instance.data.formats

import com.mineinabyss.launchy.instance.data.InstanceModList
import com.mineinabyss.launchy.instance.data.InstanceModLoaders
import com.mineinabyss.launchy.instance.data.Mod
import com.mineinabyss.launchy.instance.data.ModGroup
import com.mineinabyss.launchy.instance.data.storage.ModConfig
import com.mineinabyss.launchy.util.GroupName
import kotlinx.serialization.Serializable
import java.nio.file.Path

@Serializable
data class LaunchyPackFormat(
    val fabricVersion: String? = null,
    val minecraftVersion: String,
    val groups: Set<ModGroup>,
    private val modGroups: Map<GroupName, Set<ModConfig>>,
) : PackFormat {
    override fun toGenericMods(downloadsDir: Path): InstanceModList {
        return InstanceModList(
            modGroups
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
