package com.mineinabyss.launchy.data.modpacks.types

import com.mineinabyss.launchy.data.modpacks.ModInfo
import com.mineinabyss.launchy.data.modpacks.Mod
import com.mineinabyss.launchy.data.modpacks.Mods
import com.mineinabyss.launchy.data.modpacks.PackDependencies
import kotlinx.serialization.Serializable
import java.nio.file.Path

@Serializable
data class ModrinthPackFormat(
    val dependencies: PackDependencies,
    val files: List<PackFile>,
    val formatVersion: Int,
    val name: String,
    val versionId: String,
) : PackFormat {
    @Serializable
    data class PackFile(
        val downloads: List<String>,
        val fileSize: Long,
        val path: String,
    ) {
        fun toMod(packDir: Path) = Mod(
            packDir,
            ModInfo(
                name = path.removePrefix("mods/").removeSuffix(".jar"),
                desc = "Unknown",
                url = downloads.single(),
                downloadPath = path,
            )
        )
    }

    override fun getDependencies(minecraftDir: Path): PackDependencies {
        return dependencies
    }

    override fun toGenericMods(minecraftDir: Path) =
        Mods.withSingleGroup(files.map { it.toMod(minecraftDir) })
}

