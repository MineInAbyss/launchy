package com.mineinabyss.launchy.instance.data.formats

import com.mineinabyss.launchy.instance.data.InstanceModList
import com.mineinabyss.launchy.instance.data.InstanceModLoaders
import java.nio.file.Path

sealed interface PackFormat {
    fun toGenericMods(downloadsDir: Path): InstanceModList

    fun getModLoaders(): InstanceModLoaders

    fun getOverridesPaths(configDir: Path): List<Path>
}
