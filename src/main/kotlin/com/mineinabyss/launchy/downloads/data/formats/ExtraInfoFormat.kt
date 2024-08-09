package com.mineinabyss.launchy.downloads.data.formats

import com.charleskorn.kaml.decodeFromStream
import com.mineinabyss.launchy.instance.data.InstanceModel
import com.mineinabyss.launchy.instance.data.ModGroup
import com.mineinabyss.launchy.instance.data.ModListModel
import com.mineinabyss.launchy.instance.data.storage.ModConfig
import com.mineinabyss.launchy.util.Formats
import kotlinx.serialization.Serializable
import java.nio.file.Path
import kotlin.io.path.div
import kotlin.io.path.inputStream
import kotlin.io.path.isRegularFile

data class ExtraInfoFormat(
    val innerFormat: ModpackFormat,
) : ModpackFormat {
    override suspend fun prepareSource(instance: InstanceModel, download: Path) {
        innerFormat.prepareSource(instance, download)
    }

    override suspend fun loadPackFor(instance: InstanceModel): Result<Modpack> {
        val inner = innerFormat.loadPackFor(instance)

        val extraInfoFile = (instance.modpackFilesDir / "launchy.yml").takeIf { it.isRegularFile() }
        val extraInfo = extraInfoFile?.runCatching {
            Formats.yaml.decodeFromStream<ExtraPackInfo>(extraInfoFile.inputStream())
        }?.getOrNull() ?: return inner

        return inner.map { pack ->
            val originalMods = pack.modList.mods
            val foundMods = mutableSetOf<Mod>()
            val mods: Map<ModGroup, Set<Mod>> = extraInfo.modGroups
                .mapKeys { (name, _) -> pack.modList.groups.single { it.name == name } }
                .mapValues { (_, mods) ->
                    mods.mapNotNull { ref ->
                        val found = originalMods.find { mod -> ref.urlContains in mod.info.url }
                        if (found != null) foundMods.add(found)
                        if (found != null && ref.info != null)
                            found.copy(info = ref.info.copy(url = found.info.url))
                        else found
                    }.toSet()
                }

            val originalGroups = pack.modList.modGroups.mapValues {
                it.value.filterTo(mutableSetOf()) { mod -> mod !in foundMods }
            }

            pack.copy(
                modList = ModListModel((originalGroups + mods)
                    .filter { (_, mods) -> mods.isNotEmpty() })
            )
        }
    }

    @Serializable
    class ModReference(
        val urlContains: String,
        val info: ModConfig? = null,
    )

    @Serializable
    class ExtraPackInfo(
        val groups: List<ModGroup> = listOf(),
        val modGroups: Map<String, Set<ModReference>> = mapOf(),
    )
}
