package com.mineinabyss.launchy.instance.data

import java.nio.file.Path

class Modpack(
    val modLoaders: InstanceModLoaders,
    val mods: Mods,
    val overridesPaths: List<Path> = listOf(),
)
