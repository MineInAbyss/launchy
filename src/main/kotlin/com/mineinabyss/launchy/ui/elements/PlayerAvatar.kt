package com.mineinabyss.launchy.ui.elements

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.data.config.PlayerProfile

@Composable
fun PlayerAvatar(profile: PlayerProfile, modifier: Modifier = Modifier) {
    val state = LocalLaunchyState
    val avatar: BitmapPainter? by profile.getAvatar(state)
    if (avatar != null) Image(
        painter = avatar!!,
        contentDescription = "Avatar",
        contentScale = ContentScale.FillWidth,
        modifier = modifier
    )
}
