package com.mineinabyss.launchy.ui.elements

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ComfyWidth(
    content: @Composable () -> Unit
) {
    Box(
        Modifier.fillMaxWidth().padding(end = 16.dp, top = 16.dp, bottom = 16.dp),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Box(Modifier.width(800.dp)) {
            content()
        }
    }
}

@Composable
fun ComfyContent(
    content: @Composable () -> Unit
) {
    ComfyWidth {
        Surface(
            tonalElevation = 1.dp,
            shape = RoundedCornerShape(20.dp)
        ) {
            Box(Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}
