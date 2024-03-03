package com.mineinabyss.launchy.ui.colors

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.mineinabyss.launchy.ui.LaunchyTypography

var currentHue by mutableStateOf(0f)

@Composable
fun AppTheme(
    useDarkTheme: Boolean = true,
    content: @Composable() () -> Unit
) {
    val animatedHue by animateFloatAsState(currentHue, animationSpec = tween(durationMillis = 1000))
    val colors = LaunchyColors(animatedHue)

    MaterialTheme(
        colorScheme = colors.DarkColors,
        content = content,
        typography = LaunchyTypography
    )
}
