package com.mineinabyss.launchy.data.config

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import com.mineinabyss.launchy.data.Dirs
import com.mineinabyss.launchy.data.serializers.UUIDSerializer
import com.mineinabyss.launchy.logic.Downloader
import com.mineinabyss.launchy.state.LaunchyState
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.*
import kotlin.io.path.inputStream

@Serializable
data class PlayerProfile(
    val name: String,
    val uuid: @Serializable(with = UUIDSerializer::class) UUID,
) {
    @Transient
    private val avatar = mutableStateOf<BitmapPainter?>(null)

    @Composable
    fun getAvatar(state: LaunchyState): MutableState<BitmapPainter?> = remember {
        avatar.also {
            if (it.value != null) return@also
            state.ioScope.launch {
                Downloader.downloadAvatar(uuid)
                it.value =
                    BitmapPainter(loadImageBitmap(Dirs.avatar(uuid).inputStream()), filterQuality = FilterQuality.None)
            }
        }
    }
}
