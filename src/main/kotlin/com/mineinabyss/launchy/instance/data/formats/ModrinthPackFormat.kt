package com.mineinabyss.launchy.instance.data.formats

import com.mineinabyss.launchy.instance.data.InstanceModList
import com.mineinabyss.launchy.instance.data.InstanceModLoaders
import com.mineinabyss.launchy.instance.data.Mod
import com.mineinabyss.launchy.instance.data.storage.ModConfig
import kotlinx.serialization.Serializable
import java.nio.file.Path
import kotlin.io.path.div

@Serializable
data class ModrinthPackFormat(
    val dependencies: InstanceModLoaders,
    val files: List<PackFile>,
    val formatVersion: Int,
    val name: String,
    val versionId: String,
) : PackFormat {
    @Serializable
    data class PackFile(
        val downloads: List<String>,
        val fileSize: Long,
        val path: ModDownloadPath,
        val hashes: Hashes,
    ) {
        fun toMod(packDir: Path) = Mod(
            packDir,
            ModConfig(
                name = path.validated.toString().removePrefix("mods/").removeSuffix(".jar"),
                desc = "",
                url = downloads.single(),
                downloadPath = path,
            ),
            modId = downloads.single().removePrefix("https://cdn.modrinth.com/data/").substringBefore("/versions"),
            desiredHashes = hashes,
        )
    }

    @Serializable
    data class Hashes(
        val sha1: String,
        val sha512: String,
    )

    override fun getModLoaders(): InstanceModLoaders {
        return dependencies
    }

    override fun toGenericMods(downloadsDir: Path) =
        InstanceModList.withSingleGroup(files.map { it.toMod(downloadsDir) })

    override fun getOverridesPaths(configDir: Path): List<Path> = listOf(configDir / "mrpack" / "overrides")
}

