package com.mineinabyss.launchy.ui.elements

import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.data.config.PlayerProfile
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun PlayerAvatar(profile: PlayerProfile, modifier: Modifier = Modifier) {
    var avatar: BitmapPainter? by remember { mutableStateOf(null) }
    val state = LocalLaunchyState
    LaunchedEffect(profile) {
            avatar = profile.getAvatar(state)
    }
    if (avatar != null) Image(
        painter = avatar!!,
        contentDescription = "Avatar",
        contentScale = ContentScale.FillWidth,
        modifier = modifier
    )
}
