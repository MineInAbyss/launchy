package com.mineinabyss.launchy.instance.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.core.ui.LocalGameInstanceState

@Composable
fun LogoLarge(painter: BitmapPainter?, modifier: Modifier) {
    LocalLaunchyState
    val pack = LocalGameInstanceState
    AnimatedVisibility(
        painter != null,
        enter = fadeIn() + expandVertically(clip = false) + fadeIn(),
        modifier = Modifier.widthIn(0.dp, 500.dp).then(modifier)
    ) {
        if (painter == null) return@AnimatedVisibility
        Image(
            painter = painter!!,
            contentDescription = "Modpack logo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillWidth
        )
    }
}
