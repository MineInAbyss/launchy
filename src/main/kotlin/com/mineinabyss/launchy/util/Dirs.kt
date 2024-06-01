package com.mineinabyss.launchy.util

import com.mineinabyss.launchy.config.data.GameInstance
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

    fun cacheDir(instance: GameInstance) = instance.configDir / "cache"

    val imageCache = config / "cache" / "images"

    val jdks = mineinabyss / ".jdks"

    val accounts = config / "accounts"

    fun avatar(uuid: UUID) = imageCache / "avatar-$uuid"

    val configFile = config / "mia-launcher.yml"

    val modpackConfigsDir = (config / "modpacks")

    val modpacksDir = mineinabyss / "modpacks"
    fun modpackDir(string: String) = modpacksDir / string
    fun modpackConfigDir(name: String) = modpackConfigsDir / name

    fun createDirs() {
        config.createDirectories()
        mineinabyss.createDirectories()
        modpackConfigsDir.createDirectories()
        jdks.createDirectories()
        imageCache.createDirectories()
    }

    fun createConfigFiles() {
        if (configFile.notExists())
            configFile.createFile().writeText("{}")
    }

    fun createTempCloudInstanceFile() = createTempFile(prefix = "cloudInstance", suffix = ".yml")
}
