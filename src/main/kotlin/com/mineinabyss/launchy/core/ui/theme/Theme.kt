package com.mineinabyss.launchy.core.ui.theme

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

var currentHue by mutableStateOf(0f)

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    val animatedHue by animateFloatAsState(currentHue, animationSpec = tween(durationMillis = 500))
    val colors = LaunchyColors(animatedHue)

    MaterialTheme(
        colorScheme = colors.DarkColors,
        content = content,
        typography = LaunchyTypography
    )
}
