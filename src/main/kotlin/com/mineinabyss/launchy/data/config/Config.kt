package com.mineinabyss.launchy.data.config

import com.mineinabyss.launchy.data.Dirs
import com.mineinabyss.launchy.data.Formats
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import java.io.*
import java.nio.file.Path
import java.util.zip.ZipFile
import kotlin.io.path.*


@Serializable
data class Config(
    val handledImportOptions: Boolean = false,
    val onboardingComplete: Boolean = false,
    val currentProfile: PlayerProfile? = null,
    val javaPath: String? = null,
    val jvmArguments: String? = null,
    val memoryAllocation: Int? = null,
    val useRecommendedJvmArguments: Boolean = true,
) {
    fun save() {
        Dirs.configFile.writeText(Formats.yaml.encodeToString(this))
    }

    companion object {
        fun read() =
            Formats.yaml.decodeFromStream(serializer(), Dirs.configFile.inputStream())
    }
}

@Throws(IOException::class)
fun unzip(zipFilePath: Path, destDirectory: Path) {
    if (destDirectory.notExists()) destDirectory.createDirectories()

    ZipFile(zipFilePath.toFile()).use { zip ->
        zip.entries().asSequence().forEach { entry ->
            zip.getInputStream(entry).use { input ->
                val filePath = destDirectory / entry.name
                filePath.createParentDirectories()
                if (!entry.isDirectory) extractFile(input, filePath)
                else {
                    if (filePath.notExists()) filePath.createDirectory()
                }
            }
        }
    }
}

@Throws(IOException::class)
fun extractFile(inputStream: InputStream, destFilePath: Path) {
    val bufferSize = 4096
    val buffer = BufferedOutputStream(destFilePath.outputStream())
    val bytes = ByteArray(bufferSize)
    var read: Int
    while (inputStream.read(bytes).also { read = it } != -1) {
        buffer.write(bytes, 0, read)
    }
    buffer.close()
}

