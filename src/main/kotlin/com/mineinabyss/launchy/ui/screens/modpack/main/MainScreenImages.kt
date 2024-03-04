package com.mineinabyss.launchy.ui.screens.modpack.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowScope
import com.mineinabyss.launchy.ui.screens.LocalModpackState
import org.jetbrains.skia.Bitmap

@Composable
fun BoxScope.BackgroundImage(windowScope: WindowScope) {
    val pack = LocalModpackState
    val background by produceState<BitmapPainter?>(null) {
        value = pack.modpack.info.getOrDownloadBackground()
    }
    if(background == null) return
    windowScope.WindowDraggableArea {
        Image(
            painter = background!!,
            contentDescription = "Modpack background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
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
fun BoxScope.SlightBackgroundTint() {
    val colors = listOf(
        Color.Transparent,
        MaterialTheme.colorScheme.background.copy(alpha = 0.9f),
    )

    BoxWithConstraints(Modifier.align(Alignment.BottomCenter)) {
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(maxHeight / 1.5f)
                .background(Brush.verticalGradient(colors))
        )
    }
}

@Composable
fun LogoLarge(modifier: Modifier) {
    val pack = LocalModpackState
    val painter by produceState<BitmapPainter?>(null) {
        value =  pack.modpack.info.getOrDownloadLogo()
    }
    if(painter == null) return
    Image(
        painter = painter!!,
        contentDescription = "Modpack logo",
        modifier = Modifier.widthIn(0.dp, 500.dp).fillMaxSize().then(modifier),
        contentScale = ContentScale.FillWidth
    )
}
