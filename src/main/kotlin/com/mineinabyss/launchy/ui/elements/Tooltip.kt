package com.mineinabyss.launchy.ui.elements

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Tooltip(text: String) {
    Tooltip { Text(text, style = MaterialTheme.typography.labelMedium) }
}

@Composable
fun Tooltip(content: @Composable () -> Unit) {
    Surface(
        color = Color.Black.copy(alpha = 0.75f),
        shape = RoundedCornerShape(4.dp),
    ) {
        Box(Modifier.padding(4.dp)) {
            content()
        }
    }
}
