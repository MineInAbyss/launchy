package com.mineinabyss.launchy.downloads.data.formats

import com.mineinabyss.launchy.instance.data.InstanceModel
import com.mineinabyss.launchy.instance.data.ModListModel
import com.mineinabyss.launchy.instance.data.ModLoaderModel
import com.mineinabyss.launchy.instance.data.storage.ModConfig
import com.mineinabyss.launchy.util.Formats
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.decodeFromStream
import org.rauschig.jarchivelib.ArchiverFactory
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.deleteRecursively
import kotlin.io.path.div
import kotlin.io.path.inputStream

class ModrinthPackFormat : ModpackFormat {
    @OptIn(ExperimentalPathApi::class)
    override suspend fun prepareSource(instance: InstanceModel, download: Path) {
        val unzipDest = instance.modpackFilesDir
        unzipDest.deleteRecursively()
        ArchiverFactory.createArchiver("zip").extract(download.toFile(), unzipDest.toFile())
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun loadPackFor(instance: InstanceModel): Result<Modpack> {
        val unzipDest = instance.modpackFilesDir
        val index = unzipDest / "modrinth.index.json"
        val mrpack = Formats.json.decodeFromStream<SerializedModrinthPack>(index.inputStream())

        return runCatching {
            Modpack(
                modLoader = mrpack.dependencies,
                modList = ModListModel.withSingleGroup(mrpack.files.map { it.toMod() }),
                configSources = listOf(unzipDest / "overrides"),
            )
        }
    }

    @Serializable
    data class SerializedModrinthPack(
        val dependencies: ModLoaderModel,
        val files: List<PackFile>,
        val formatVersion: Int,
        val name: String,
        val versionId: String,
    ) {
        @Serializable
        data class PackFile(
            val downloads: List<String>,
            val fileSize: Long,
            val path: ModDownloadPath,
            val hashes: Hashes,
        ) {
            fun toMod() = Mod(
                modId = downloads.single().removePrefix("https://cdn.modrinth.com/data/").substringBefore("/versions"),
                ModConfig(
                    name = path.validated.toString().removePrefix("mods/").removeSuffix(".jar"),
                    desc = "",
                    url = downloads.single(),
                    downloadPath = path,
                ),
                desiredHashes = hashes,
            )
        }
    }
}

