package com.mineinabyss.launchy.data

import com.mineinabyss.launchy.util.OS
import java.util.UUID
import kotlin.io.path.*

object Dirs {
    val home = Path(System.getProperty("user.home"))
    val minecraft = when (OS.get()) {
        OS.WINDOWS -> Path(System.getenv("APPDATA")) / ".minecraft"
        OS.MAC -> Path(System.getProperty("user.home")) / "Library/Application Support/minecraft"
        OS.LINUX -> Path(System.getProperty("user.home")) / ".minecraft"
    }

    val mineinabyss = when (OS.get()) {
        OS.WINDOWS -> Path(System.getenv("APPDATA")) / ".mineinabyss"
        OS.MAC -> Path(System.getProperty("user.home")) / "Library/Application Support/mineinabyss"
        OS.LINUX -> Path(System.getProperty("user.home")) / ".mineinabyss"
    }

    val config = when (OS.get()) {
        OS.WINDOWS -> Path(System.getenv("APPDATA"))
        OS.MAC -> Path(System.getProperty("user.home")) / "Library/Application Support"
        OS.LINUX -> home / ".config"
    } / "mineinabyss"

    val tmp = config / ".tmp"

    val accounts = config / "accounts"
    val avatars = config / "avatars"

    fun avatar(uuid: UUID) = avatars / "$uuid.png"

    val configFile = config / "mia-launcher.yml"
    val versionsFile = config / "mia-versions.yml"

    val modpackConfigsDir = (config / "modpacks")

    fun modpackDir(string: String) = mineinabyss / "modpacks" / string
    fun modpackConfigDir(name: String) = modpackConfigsDir / name

    fun createDirs() {
        config.createDirectories()
        mineinabyss.createDirectories()
        tmp.createDirectories()
        modpackConfigsDir.createDirectories()
    }

    fun createConfigFiles() {
        if (configFile.notExists())
            configFile.createFile().writeText("{}")
        if (versionsFile.notExists())
            versionsFile.createFile().writeText("{}")
    }
}
