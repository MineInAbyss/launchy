package com.mineinabyss.launchy.data.modpacks

import com.mineinabyss.launchy.data.config.GameInstance
import java.nio.file.Path

class Modpack(
    val dependencies: PackDependencies,
    val mods: Mods,
    val overridesPath: Path? = null,
)
