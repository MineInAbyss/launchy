package com.mineinabyss.launchy.instance.data.formats

import com.mineinabyss.launchy.instance.data.Mod
import com.mineinabyss.launchy.instance.data.ModGroup
import com.mineinabyss.launchy.instance.data.Mods
import java.nio.file.Path


data class ExtraInfoFormat(
    val format: PackFormat,
    val extraInfoPack: ExtraPackInfo,
) : PackFormat by format {
    override fun toGenericMods(downloadsDir: Path): Mods {
        val originalMods = format.toGenericMods(downloadsDir)
        val foundMods = mutableSetOf<Mod>()
        val mods: Map<ModGroup, Set<Mod>> = extraInfoPack.modGroups
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
