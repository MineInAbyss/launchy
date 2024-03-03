package com.mineinabyss.launchy.ui.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import com.mineinabyss.launchy.data.config.PlayerProfile

@Composable
fun PlayerAvatar(profile: PlayerProfile, modifier: Modifier = Modifier) {
    var avatar: BitmapPainter? by remember { mutableStateOf(null) }
    LaunchedEffect(profile) {
        avatar = BitmapPainter(profile.getAvatar(), filterQuality = FilterQuality.None)
    }
    if (avatar != null) Image(
        painter = avatar!!,
        contentDescription = "Avatar",
        contentScale = ContentScale.FillWidth,
        modifier = modifier
    )
}
