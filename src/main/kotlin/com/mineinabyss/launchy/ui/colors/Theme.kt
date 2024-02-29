package com.mineinabyss.launchy.ui.colors

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import com.mineinabyss.launchy.ui.LaunchyTypography

@Composable
fun AppTheme(
    useDarkTheme: Boolean = true,
    content: @Composable() () -> Unit
) {
    val colors = LaunchyColors()

    MaterialTheme(
        colorScheme = colors.DarkColors,
        content = content,
        typography = LaunchyTypography
    )
}
