package com.mineinabyss.launchy.data

import com.mineinabyss.launchy.util.OS
import kotlin.io.path.*

object Dirs {
    val home = Path(System.getProperty("user.home"))
    val minecraft = when (OS.get()) {
        OS.WINDOWS -> Path(System.getenv("APPDATA")) / ".minecraft"
        OS.MAC -> Path(System.getProperty("user.home")) / "Library/Application Support/minecraft"
        OS.LINUX -> Path(System.getProperty("user.home")) / ".minecraft"
    }

    val mineinabyss = when (OS.get()) {
        OS.WINDOWS -> Path(System.getenv("APPDATA")) / ".mineinabyss_1_19_4"
        OS.MAC -> Path(System.getProperty("user.home")) / "Library/Application Support/mineinabyss_1_19_4"
        OS.LINUX -> Path(System.getProperty("user.home")) / ".mineinabyss_1_19_4"
    }
    val mods = mineinabyss / "mods"
    val tmp = mineinabyss / ".tmp"

    val config = when (OS.get()) {
        OS.WINDOWS -> Path(System.getenv("APPDATA"))
        OS.MAC -> Path(System.getProperty("user.home")) / "Library/Application Support"
        OS.LINUX -> home / ".config"
    } / "mineinabyss"

    val configFile = config / "mia-launcher_1_19_4.yml"
    val versionsFile = config / "mia-versions_1_19_4.yml"

    fun createDirs() {
        config.createDirectories()
        mineinabyss.createDirectories()
        mods.createDirectories()
        tmp.createDirectories()
    }

    fun createConfigFiles() {
        if (configFile.notExists())
            configFile.createFile().writeText("{}")
        if (versionsFile.notExists())
            versionsFile.createFile().writeText("{}")
    }
}
