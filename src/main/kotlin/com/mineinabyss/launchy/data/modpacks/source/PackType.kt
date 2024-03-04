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

    fun getFilePath(modpackDir: Path): Path {
        val ext = when (this) {
            Launchy -> "yml"
            Modrinth -> "zip"
        }
        return modpackDir / "pack.$ext"
    }
    @OptIn(ExperimentalSerializationApi::class)
    fun getFormat(file: Path): PackFormat? {
        if (!file.isRegularFile()) return null
        return when (this) {
            Launchy -> {
                Formats.yaml.decodeFromStream<LaunchyPackFormat>(file.inputStream())
            }

            Modrinth -> {
                ZipFile(file.toFile()).use { zip ->
                    zip.getEntry("modrinth.index.json")?.let {
                        Formats.json.decodeFromStream<ModrinthPackFormat>(zip.getInputStream(it))
                    }
                }
            }
        }
    }
}
