package com.mineinabyss.launchy.data.modpacks.types

import com.mineinabyss.launchy.data.modpacks.Mods
import java.nio.file.Path

interface PackFormat {
    fun toGenericMods(packDir: Path): Mods
}
