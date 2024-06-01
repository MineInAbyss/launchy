package com.mineinabyss.launchy.instance.data

import com.mineinabyss.launchy.instance.data.formats.ModrinthPackFormat
import io.ktor.http.*
import java.nio.file.Path
import kotlin.io.path.div

data class Mod(
    private val downloadDir: Path,
    val info: ModConfig,
    val modId: String,
    val desiredHashes: ModrinthPackFormat.Hashes?,
) {
    val absoluteDownloadDest =
        if (info.downloadPath != null) downloadDir / info.downloadPath.validated
        else downloadDir / "mods" / "${info.id ?: info.name}.jar"

    val downloadUrl: Url = Url(info.url)

    fun compatibleWith(other: Mod) =
        other.info.name !in info.incompatibleWith && info.name !in other.info.incompatibleWith
}
