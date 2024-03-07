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

sealed class Arch(
    val openJDKArch: String
) {
    data object X64: Arch("x64")
    data object X86: Arch("x86")
    data object ARM64: Arch("aarch64")
    data object ARM32: Arch("arm")
    data object Unknown: Arch("unknown")



    companion object {
        fun get(): Arch {
            val archString = System.getProperty("os.arch", "unknown")
            return when(archString) {
                "amd64", "x86_64" -> X64
                "x86" -> X86
                "aarch64" -> ARM64
                "arm" -> ARM32
                else -> Unknown
            }
        }
    }
}
