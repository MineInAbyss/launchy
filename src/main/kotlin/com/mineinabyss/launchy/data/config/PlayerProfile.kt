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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Transient
    private val downloadScope = CoroutineScope(Dispatchers.IO.limitedParallelism(1))

    @Composable
    fun getAvatar(): MutableState<BitmapPainter?> = remember {
        avatar.also {
            if (it.value != null) return@also
            downloadScope.launch {
                Downloader.downloadAvatar(uuid)
                it.value = BitmapPainter(
                    loadImageBitmap(Dirs.avatar(uuid).inputStream()),
                    filterQuality = FilterQuality.None
                )
            }
        }
    }
}
