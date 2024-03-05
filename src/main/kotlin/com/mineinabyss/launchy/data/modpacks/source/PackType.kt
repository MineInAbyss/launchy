package com.mineinabyss.launchy.data.modpacks.source

import com.charleskorn.kaml.decodeFromStream
import com.mineinabyss.launchy.data.Formats
import com.mineinabyss.launchy.data.modpacks.types.LaunchyPackFormat
import com.mineinabyss.launchy.data.modpacks.types.ModrinthPackFormat
import com.mineinabyss.launchy.data.modpacks.types.PackFormat
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.decodeFromStream
import java.nio.file.Path
import java.util.zip.ZipFile
import kotlin.io.path.div
import kotlin.io.path.inputStream
import kotlin.io.path.isRegularFile

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

    @OptIn(ExperimentalSerializationApi::class)
    fun getFormat(file: Path): Result<PackFormat> {
        if (!file.isRegularFile()) return Result.failure(IllegalStateException("Could not find modpack file at $file"))
        return when (this) {
            Launchy -> runCatching { Formats.yaml.decodeFromStream<LaunchyPackFormat>(file.inputStream()) }

            Modrinth -> runCatching {
                ZipFile(file.toFile()).use { zip ->
                    val index = zip.getEntry("modrinth.index.json")
                        ?: return Result.failure(IllegalStateException("Could not find modrinth.index.json in $file"))
                    Formats.json.decodeFromStream<ModrinthPackFormat>(zip.getInputStream(index))
                }
            }
        }
    }
}
