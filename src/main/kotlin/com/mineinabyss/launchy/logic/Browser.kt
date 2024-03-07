package com.mineinabyss.launchy.logic

import com.mineinabyss.launchy.util.OS
import java.awt.Desktop
import java.net.URI

object Browser {
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
}
