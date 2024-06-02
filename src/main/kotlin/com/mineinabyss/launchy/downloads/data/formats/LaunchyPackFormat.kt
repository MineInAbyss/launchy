package com.mineinabyss.launchy.downloads.data.formats

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.decodeFromStream
import com.mineinabyss.launchy.instance.data.InstanceModel
import com.mineinabyss.launchy.instance.data.ModGroup
import com.mineinabyss.launchy.instance.data.ModListModel
import com.mineinabyss.launchy.instance.data.ModLoaderModel
import com.mineinabyss.launchy.instance.data.storage.ModConfig
import com.mineinabyss.launchy.util.GroupName
import kotlinx.serialization.Serializable
import java.nio.file.Path
import kotlin.io.path.copyTo
import kotlin.io.path.div
import kotlin.io.path.inputStream

class LaunchyPackFormat : ModpackFormat {
    override suspend fun prepareSource(instance: InstanceModel, download: Path) {
        download.copyTo(instance.modpackFilesDir / "pack.yml")
    }

    override suspend fun loadPackFor(instance: InstanceModel): Result<Modpack> = runCatching {
        val pack = Yaml.Companion.default.decodeFromStream<SerializedLaunchyPack>(
            (instance.modpackFilesDir / "pack.yml").inputStream()
        )
        Modpack(
            modLoader = ModLoaderModel(minecraft = pack.minecraftVersion, fabricLoader = pack.fabricVersion),
            modList = ModListModel(
                pack.modGroups
                    .mapKeys { (name, _) -> pack.groups.single { it.name == name } }
                    .mapValues { (_, mods) ->
                        mods.map {
                            Mod(
                                info = it,
                                modId = it.id ?: it.name,
                                desiredHashes = null,
                            )
                        }.toSet()
                    }),
            configSources = emptyList(),
        )
    }

    @Serializable
    data class SerializedLaunchyPack(
        val fabricVersion: String? = null,
        val minecraftVersion: String,
        val groups: Set<ModGroup>,
        val modGroups: Map<GroupName, Set<ModConfig>>,
    )
}
