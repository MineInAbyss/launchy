package com.mineinabyss.launchy.data.modpacks

import com.mineinabyss.launchy.data.Dirs
import java.nio.file.Path
import kotlin.io.path.div
import kotlin.io.path.exists

data class Mod(
    val packDir: Path,
    val info: ModInfo
) {
    val file =
        if (info.downloadPath != null) packDir / info.downloadPath
        else packDir / "mods" / "${info.name}.jar"

    val config = Dirs.tmp / "${info.name}-config.zip"

    val isDownloaded get() = file.exists()


    fun compatibleWith(other: Mod) =
        other.info.name !in info.incompatibleWith && info.name !in other.info.incompatibleWith
}
