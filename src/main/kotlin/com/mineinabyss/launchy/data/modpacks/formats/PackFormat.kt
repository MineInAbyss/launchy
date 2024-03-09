package com.mineinabyss.launchy.data.modpacks.formats

import com.mineinabyss.launchy.data.modpacks.InstanceModLoaders
import com.mineinabyss.launchy.data.modpacks.Mods
import java.nio.file.Path

sealed interface PackFormat {
    fun toGenericMods(downloadsDir: Path): Mods

    fun getModLoaders(): InstanceModLoaders

    fun getOverridesPaths(configDir: Path): List<Path>
}
