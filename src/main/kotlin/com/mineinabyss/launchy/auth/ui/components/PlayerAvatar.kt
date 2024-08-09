package com.mineinabyss.launchy.auth.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import com.mineinabyss.launchy.auth.ui.ProfileUiState

@Composable
fun PlayerAvatar(
    profile: ProfileUiState?,
    modifier: Modifier = Modifier
) {
    val avatar = profile?.avatar
    if (avatar == null) {
        val missingSkin = remember {
            useResource("missing_skin.png") {
                BitmapPainter(
                    loadImageBitmap(it),
                    filterQuality = FilterQuality.None
                )
            }
        }
        Image(missingSkin, "Not logged in", Modifier.fillMaxSize())
    } else Image(
        painter = avatar,
        contentDescription = "Avatar",
        contentScale = ContentScale.FillWidth,
        modifier = modifier
    )
}
