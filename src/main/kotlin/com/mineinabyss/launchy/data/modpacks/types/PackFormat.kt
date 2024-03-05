package com.mineinabyss.launchy.data.modpacks.types

import com.mineinabyss.launchy.data.modpacks.Mods
import com.mineinabyss.launchy.data.modpacks.PackDependencies
import java.nio.file.Path

sealed interface PackFormat {
    fun toGenericMods(minecraftDir: Path): Mods

    fun getDependencies(minecraftDir: Path): PackDependencies
}
