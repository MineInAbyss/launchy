package com.mineinabyss.launchy.data.config

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.loadImageBitmap
import com.mineinabyss.launchy.data.Dirs
import com.mineinabyss.launchy.data.serializers.UUIDSerializer
import com.mineinabyss.launchy.logic.Downloader
import kotlinx.serialization.Serializable
import java.util.*
import kotlin.io.path.exists
import kotlin.io.path.inputStream

@Serializable
data class PlayerProfile(
    val name: String,
    val uuid: @Serializable(with = UUIDSerializer::class) UUID,
) {
    suspend fun getAvatar(): ImageBitmap {
        val avatarPath = Dirs.avatar(uuid)

        if (!avatarPath.exists()) Downloader.downloadAvatar(uuid)

        return loadImageBitmap(avatarPath.inputStream())
    }
}
