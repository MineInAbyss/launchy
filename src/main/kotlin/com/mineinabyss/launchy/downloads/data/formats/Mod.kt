package com.mineinabyss.launchy.downloads.data.formats

import com.mineinabyss.launchy.instance.data.storage.ModConfig
import com.mineinabyss.launchy.util.ModID

data class Mod(
    val modId: ModID,
    val info: ModConfig,
    val desiredHashes: Hashes?,
) {
    //    val absoluteDownloadDest =
//        if (info.downloadPath != null) downloadDir / info.downloadPath.validated
//        else downloadDir / "mods" / "${info.id ?: info.name}.jar"
//
//    val downloadUrl: Url = Url(info.url)
//
    fun compatibleWith(other: Mod) =
        other.info.name !in info.incompatibleWith && info.name !in other.info.incompatibleWith
}
