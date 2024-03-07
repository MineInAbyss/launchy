package com.mineinabyss.launchy.ui.elements

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.ui.screens.screen

@Composable
fun ComfyWidth(
    content: @Composable () -> Unit
) {
    val endDp = if (screen.showSidebar) 16.dp else 0.dp
    Box(
        Modifier.fillMaxWidth().padding(end = endDp),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Box(Modifier.width(800.dp)) {
            content()
        }
    }
}

@Composable
fun ComfyTitle(
    title: String
) = ComfyWidth {
    Box(Modifier.padding(top = 16.dp, bottom = 8.dp)) {
        Text(title, style = MaterialTheme.typography.headlineMedium)
    }
}

@Composable
fun ComfyContent(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    ComfyWidth {
        Surface(
            tonalElevation = 1.dp,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp).then(modifier)
        ) {
            Box(Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp)) {
                content()
            }
        }
    }
}
