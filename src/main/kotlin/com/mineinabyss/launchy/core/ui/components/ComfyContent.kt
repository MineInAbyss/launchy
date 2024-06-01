package com.mineinabyss.launchy.core.ui.components

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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.core.ui.screen

@Composable
fun ComfyWidth(
    overrideWidth: Dp? = null,
    content: @Composable () -> Unit,
) {
    val endDp = if (screen.showSidebar) 16.dp else 0.dp
    Box(
        Modifier.fillMaxWidth().padding(end = endDp),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Box(Modifier.width(overrideWidth ?: 800.dp)) {
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
    overrideWidth: Dp? = null,
    content: @Composable () -> Unit,
) {
    ComfyWidth(overrideWidth) {
        Surface(
            tonalElevation = 1.dp,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp).then(modifier)
        ) {
            Box(Modifier.padding(20.dp)) {
                content()
            }
        }
    }
}
