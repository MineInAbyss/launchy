package com.mineinabyss.launchy.util

import java.awt.Desktop
import java.net.URI
import java.nio.file.Path

object DesktopHelpers {
    val desktop = Desktop.getDesktop()
    fun browse(url: String): Result<*> = synchronized(desktop) {
        val os = OS.get()
        runCatching {
            when {
                Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.BROWSE) -> desktop.browse(URI.create(url))
                 os == OS.LINUX -> Runtime.getRuntime().exec("xdg-open $url")
                os == OS.MAC -> Runtime.getRuntime().exec("open $url")
                else -> error("Unsupported OS")
            }
        }
    }

    fun openDirectory(path: Path) {
        val os = OS.get()
        runCatching {
            when {
                Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.OPEN) -> desktop.open(path.toFile())
                os == OS.LINUX -> Runtime.getRuntime().exec("xdg-open $path")
                os == OS.MAC -> Runtime.getRuntime().exec("open $path")
                else -> error("Unsupported OS")
            }
        }
    }
}
