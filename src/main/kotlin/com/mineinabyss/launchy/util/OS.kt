package com.mineinabyss.launchy.util

enum class OS {
    WINDOWS, MAC, LINUX;

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
