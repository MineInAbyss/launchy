package com.mineinabyss.launchy.instance.data

import java.nio.file.Path

class Modpack(
    val modLoaders: InstanceModLoaders,
    val mods: InstanceModList,
    val overridesPaths: List<Path> = listOf(),
)
