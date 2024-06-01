package com.mineinabyss.launchy.instance.data.formats

import com.mineinabyss.launchy.instance.data.InstanceModLoaders
import com.mineinabyss.launchy.instance.data.Mods
import java.nio.file.Path

sealed interface PackFormat {
    fun toGenericMods(downloadsDir: Path): Mods

    fun getModLoaders(): InstanceModLoaders

    fun getOverridesPaths(configDir: Path): List<Path>
}
