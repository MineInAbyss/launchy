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

    val hibiscusmc = when (OS.get()) {
        OS.WINDOWS -> Path(System.getenv("APPDATA")) / ".HibiscusMC"
        OS.MAC -> Path(System.getProperty("user.home")) / "Library/Application Support/HibiscusMC"
        OS.LINUX -> Path(System.getProperty("user.home")) / ".HibiscusMC"
    }
    val mods = hibiscusmc / "mods"
    val configZip = hibiscusmc / "configs.zip"

    val config = when (OS.get()) {
        OS.WINDOWS -> Path(System.getenv("APPDATA"))
        OS.MAC -> Path(System.getProperty("user.home")) / "Library/Application Support"
        OS.LINUX -> home / ".config"
    } / "hibiscusmc"

    val configFile = config / "hmc-launcher.yml"
    val versionsFile = config / "hmc-versions.yml"

    fun createDirs() {
        config.createDirectories()
        hibiscusmc.createDirectories()
    }

    fun createConfigFiles() {
        if (configFile.notExists())
            configFile.createFile().writeText("{}")
        if (versionsFile.notExists())
            versionsFile.createFile().writeText("{}")
    }
}
