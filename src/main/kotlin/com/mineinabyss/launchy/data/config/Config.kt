package com.mineinabyss.launchy.data.config

import com.mineinabyss.launchy.data.Dirs
import com.mineinabyss.launchy.data.Formats
import com.mineinabyss.launchy.data.modpacks.ModpackInfo
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import java.io.*
import java.util.zip.ZipFile
import kotlin.io.path.inputStream
import kotlin.io.path.writeText


@Serializable
data class Config(
    val handledImportOptions: Boolean = false,
    val onboardingComplete: Boolean = false,
    val currentProfile: PlayerProfile? = null,
    val modpacks: List<ModpackInfo> = emptyList(),

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
fun unzip(zipFilePath: File, destDirectory: String) {

    File(destDirectory).run {
        if (!exists()) {
            mkdirs()
        }
    }

    ZipFile(zipFilePath).use { zip ->
        zip.entries().asSequence().forEach { entry ->
            zip.getInputStream(entry).use { input ->
                val filePath = destDirectory + File.separator + entry.name

                if (!entry.isDirectory) extractFile(input, filePath)
                else File(filePath).mkdir()
            }
        }
    }
}

@Throws(IOException::class)
fun extractFile(inputStream: InputStream, destFilePath: String) {
    val bufferSize = 4096
    val buffer = BufferedOutputStream(FileOutputStream(destFilePath))
    val bytes = ByteArray(bufferSize)
    var read: Int
    while (inputStream.read(bytes).also { read = it } != -1) {
        buffer.write(bytes, 0, read)
    }
    buffer.close()
}

