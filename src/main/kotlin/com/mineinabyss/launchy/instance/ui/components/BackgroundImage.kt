package com.mineinabyss.launchy.instance.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.window.WindowScope

@Composable
fun BoxScope.BackgroundImage(painter: BitmapPainter?, windowScope: WindowScope) {
    AnimatedVisibility(painter != null, enter = fadeIn(), exit = fadeOut()) {
        if (painter == null) return@AnimatedVisibility
        windowScope.WindowDraggableArea {
            Image(
                painter = painter!!,
                contentDescription = "Modpack background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
    BackgroundTint()
}

@Composable
fun BoxScope.BackgroundTint() {
    val colors = listOf(
        Color.Transparent,
        MaterialTheme.colorScheme.background,
    )

    BoxWithConstraints(Modifier.align(Alignment.BottomCenter)) {
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(maxHeight / 2)
                .background(Brush.verticalGradient(colors))
        )
    }
}

@Composable
fun BoxScope.SlightBackgroundTint(modifier: Modifier = Modifier) {
    val colors = listOf(
        Color.Transparent,
        MaterialTheme.colorScheme.background.copy(alpha = 0.9f),
    )

    BoxWithConstraints(modifier.align(Alignment.BottomCenter)) {
        Spacer(
            modifier
                .fillMaxWidth()
                .height(maxHeight / 1.5f)
                .background(Brush.verticalGradient(colors))
        )
    }
}

