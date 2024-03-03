package com.mineinabyss.launchy.data.modpacks.types

import com.mineinabyss.launchy.data.modpacks.Mods
import com.mineinabyss.launchy.data.modpacks.PackDependencies
import java.nio.file.Path

sealed interface PackFormat {
    fun toGenericMods(packDir: Path): Mods

    fun getDependencies(packDir: Path): PackDependencies
}
