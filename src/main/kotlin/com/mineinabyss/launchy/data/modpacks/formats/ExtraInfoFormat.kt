package com.mineinabyss.launchy.data.modpacks.formats

import com.mineinabyss.launchy.data.modpacks.ExtraPackInfo
import com.mineinabyss.launchy.data.modpacks.Group
import com.mineinabyss.launchy.data.modpacks.Mod
import com.mineinabyss.launchy.data.modpacks.Mods
import java.nio.file.Path


data class ExtraInfoFormat(
    val format: PackFormat,
    val extraInfoPack: ExtraPackInfo,
) : PackFormat by format {
    override fun toGenericMods(minecraftDir: Path): Mods {
        val originalMods = format.toGenericMods(minecraftDir)
        val foundMods = mutableSetOf<Mod>()
        val mods: Map<Group, Set<Mod>> = extraInfoPack.modGroups
            .mapKeys { (name, _) -> extraInfoPack.groups.single { it.name == name } }
            .mapValues { (_, mods) ->
                mods.mapNotNull { ref ->
                    val found = originalMods.mods.find { mod -> ref.urlContains in mod.info.url }
                    if (found != null) foundMods.add(found)
                    if (found != null && ref.info != null)
                        found.copy(info = ref.info.copy(url = found.info.url))
                    else found
                }.toSet()
            }

        val originalGroups = originalMods.modGroups.mapValues {
            it.value.filterTo(mutableSetOf()) { mod -> mod !in foundMods }
        }
        return Mods((originalGroups + mods)
            .filter { (_, mods) -> mods.isNotEmpty() })
    }
}
