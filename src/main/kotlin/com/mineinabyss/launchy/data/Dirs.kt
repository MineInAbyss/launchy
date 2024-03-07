package com.mineinabyss.launchy.data

import com.mineinabyss.launchy.util.OS
import java.util.*
import kotlin.io.path.*

object Dirs {
    val home = Path(System.getProperty("user.home"))

    val minecraft = when (OS.get()) {
        OS.WINDOWS -> Path(System.getenv("APPDATA")) / ".minecraft"
        OS.MAC -> home / "Library/Application Support/minecraft"
        OS.LINUX -> home / ".minecraft"
    }

    val mineinabyss = when (OS.get()) {
        OS.WINDOWS -> Path(System.getenv("APPDATA")) / ".mineinabyss"
        OS.MAC -> home / "Library/Application Support/mineinabyss"
        OS.LINUX -> home / ".mineinabyss"
    }

    val config = when (OS.get()) {
        OS.WINDOWS -> Path(System.getenv("APPDATA"))
        OS.MAC -> home / "Library/Application Support"
        OS.LINUX -> home / ".config"
    } / "mineinabyss"

    val cacheDir = config / "cache"
    val imageCache = cacheDir / "images"

    val jdks = mineinabyss / ".jdks"

    val tmp = config / ".tmp"

    val accounts = config / "accounts"

    fun avatar(uuid: UUID) = imageCache / "avatar-$uuid"

    val configFile = config / "mia-launcher.yml"

    val modpackConfigsDir = (config / "modpacks")

    fun modpackDir(string: String) = mineinabyss / "modpacks" / string
    fun modpackConfigDir(name: String) = modpackConfigsDir / name

    fun createDirs() {
        config.createDirectories()
        mineinabyss.createDirectories()
        tmp.createDirectories()
        modpackConfigsDir.createDirectories()
        jdks.createDirectories()
        cacheDir.createDirectories()
        imageCache.createDirectories()
    }

    fun createConfigFiles() {
        if (configFile.notExists())
            configFile.createFile().writeText("{}")
    }
}
