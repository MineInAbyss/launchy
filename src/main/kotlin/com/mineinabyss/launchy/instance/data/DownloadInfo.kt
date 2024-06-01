package com.mineinabyss.launchy.instance.data

import com.mineinabyss.launchy.downloads.data.ModDownloader
import com.mineinabyss.launchy.util.hashing.Hashing.checksum
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.nio.file.Path
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.div
import kotlin.io.path.isRegularFile

@Serializable
data class DownloadInfo(
    val url: String,
    val path: String,
    val desiredHash: String?,
    val hashCheck: HashCheck,
    val result: ModDownloader.DownloadResult,
) {
    @Transient
    val systemPath = Path(path)

    fun failed(): Boolean {
        return result == ModDownloader.DownloadResult.Failed
                || systemPath.isRegularFile()
                || (desiredHash != null && hashCheck == HashCheck.FAILED)
    }

    fun calculateSha1Hash(minecraftDir: Path): String {
        val md = MessageDigest.getInstance("SHA-1")
        return (minecraftDir / systemPath).checksum(md)
    }
}
