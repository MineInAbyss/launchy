package com.mineinabyss.launchy.data.config

import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import com.mineinabyss.launchy.data.Dirs
import com.mineinabyss.launchy.data.serializers.UUIDSerializer
import com.mineinabyss.launchy.logic.Downloader
import com.mineinabyss.launchy.state.LaunchyState
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.util.*
import kotlin.io.path.inputStream

@Serializable
data class PlayerProfile(
    val name: String,
    val uuid: @Serializable(with = UUIDSerializer::class) UUID,
) {
    suspend fun getAvatar(state: LaunchyState): BitmapPainter {
        val avatarPath = Dirs.avatar(uuid)
        state.downloadContext.launch {
            Downloader.downloadAvatar(uuid)
        }.join()
        return BitmapPainter(loadImageBitmap(avatarPath.inputStream()), filterQuality = FilterQuality.None)
    }
}
