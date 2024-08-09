package com.mineinabyss.launchy.util

sealed class OS(
    val openJDKName: String
) {
    data object WINDOWS: OS("windows")
    data object LINUX: OS("linux")
    data object MAC: OS("mac")

    companion object {
        fun isArm(): Boolean {
            return System.getProperty("os.arch", "unknown").lowercase().contains("arm")
        }

        fun get(): OS {
            val os = System.getProperty("os.name").lowercase()
            return when {
                "win" in os -> WINDOWS
                "nix" in os || "nux" in os || "aix" in os -> LINUX
                "mac" in os -> MAC
                else -> error("Unsupported")
            }
        }
    }
}

