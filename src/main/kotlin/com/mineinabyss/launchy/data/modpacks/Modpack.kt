package com.mineinabyss.launchy.data.modpacks

import java.nio.file.Path

class Modpack(
    val dependencies: PackDependencies,
    val mods: Mods,
    val overridesPaths: List<Path> = listOf(),
)
