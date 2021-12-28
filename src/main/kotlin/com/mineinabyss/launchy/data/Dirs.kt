package com.mineinabyss.launchy.data

import com.mineinabyss.launchy.util.OS
import kotlin.io.path.*

object Dirs {
    val home = Path(System.getProperty("user.home"))
    val minecraft = when(OS.get()) {
        OS.WINDOWS -> Path(System.getenv("APPDATA")) / "Roaming" / ".minecraft"
        OS.MAC -> Path(System.getProperty("user.home")) / "Library/Application Support/minecraft"
        OS.LINUX -> Path(System.getProperty("user.home")) / ".minecraft"
    }
    val mods = minecraft / "mods"

    val config = when (OS.get()) {
        OS.WINDOWS -> Path(System.getenv("APPDATA")) / "Roaming"
        OS.MAC -> Path(System.getProperty("user.home")) / "Library/Application Support"
        OS.LINUX -> home / ".config"
    } / "mineinabyss"

    val configFile = config / "mia-launcher.yml"
    val versionsFile = config / "mia-versions.yml"

    fun createDirs() {
        config.createDirectories()
    }

    fun createConfigFiles() {
        if (configFile.notExists())
            configFile.createFile().writeText("{}")
        if (versionsFile.notExists())
            versionsFile.createFile().writeText("{}")
    }
}
