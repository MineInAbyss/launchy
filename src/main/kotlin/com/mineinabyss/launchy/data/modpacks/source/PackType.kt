package com.mineinabyss.launchy.data.modpacks.source

import com.charleskorn.kaml.decodeFromStream
import com.mineinabyss.launchy.data.Formats
import com.mineinabyss.launchy.data.modpacks.ExtraPackInfo
import com.mineinabyss.launchy.data.modpacks.formats.ExtraInfoFormat
import com.mineinabyss.launchy.data.modpacks.formats.LaunchyPackFormat
import com.mineinabyss.launchy.data.modpacks.formats.ModrinthPackFormat
import com.mineinabyss.launchy.data.modpacks.formats.PackFormat
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.decodeFromStream
import org.rauschig.jarchivelib.ArchiverFactory
import java.nio.file.Path
import kotlin.io.path.*

@Serializable
enum class PackType {
    Launchy, Modrinth;

    fun getFilePath(configDir: Path): Path {
        val ext = when (this) {
            Launchy -> "yml"
            Modrinth -> "zip"
        }
        return configDir / "pack.$ext"
    }

    @OptIn(ExperimentalPathApi::class)
    fun afterDownload(configDir: Path) {
        val path = getFilePath(configDir)
        if (this == Modrinth) {
            val unzipDir = configDir / "mrpack"
            unzipDir.deleteRecursively()
            ArchiverFactory.createArchiver("zip").extract(path.toFile(), unzipDir.toFile())
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun getFormat(configDir: Path): Result<PackFormat> {
        val file = getFilePath(configDir)
        return when (this) {
            Launchy -> runCatching {
                if (!file.isRegularFile()) return Result.failure(IllegalStateException("Could not find modpack file at $file"))
                Formats.yaml.decodeFromStream<LaunchyPackFormat>(file.inputStream())
            }

            Modrinth -> runCatching {
                val unzipDir = configDir / "mrpack"
                val index = unzipDir / "modrinth.index.json"
                if (unzipDir.notExists()) {
                    afterDownload(configDir)
                }
                val extraInfoFile = (unzipDir / "launchy.yml").takeIf { it.isRegularFile() }
                val extraInfo = extraInfoFile?.runCatching {
                    Formats.yaml.decodeFromStream<ExtraPackInfo>(extraInfoFile.inputStream())
                }?.getOrNull()
                val mrpack = Formats.json.decodeFromStream<ModrinthPackFormat>(index.inputStream())
                if (extraInfo != null) ExtraInfoFormat(mrpack, extraInfo)
                else mrpack
            }
        }
    }
}
