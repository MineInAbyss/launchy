package com.mineinabyss.launchy.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowScope

@Composable
fun ImportSettingsDialog(
    windowScope: WindowScope,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
) {
    // Overlay that prevents clicking behind it
    windowScope.WindowDraggableArea {
        Box(Modifier.background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)).fillMaxSize())
    }

    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier.widthIn(280.dp, 560.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Import Settings",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "This will import the options.txt file from your .minecraft directory.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(24.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    TextButton(onClick = onAccept) {
                        Text("Import")
                    }
                    TextButton(onClick = onDecline) {
                        Text("Dont Import")
                    }
                }
            }
        }
    }
}
