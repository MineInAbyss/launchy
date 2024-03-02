package com.mineinabyss.launchy.ui.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.ProvideTextStyle
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
fun LaunchyDialog(
    title: @Composable () -> Unit,
    content: @Composable () -> Unit,
    windowScope: WindowScope,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    acceptText: String,
    declineText: String?,
    modifier: Modifier = Modifier,
) {
    // Overlay that prevents clicking behind it
    windowScope.WindowDraggableArea {
        Box(Modifier.background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)).fillMaxSize())
    }

    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            modifier = modifier.widthIn(280.dp, 560.dp),
            tonalElevation = 5.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                ProvideTextStyle(MaterialTheme.typography.headlineSmall) {
                    Box(modifier = Modifier.padding(bottom = 10.dp)) {
                        title()
                    }
                }

                Spacer(Modifier.height(16.dp))
                ProvideTextStyle(MaterialTheme.typography.bodyMedium) {
                    Box(modifier = Modifier.padding(bottom = 10.dp)) {
                        content()
                    }
                }
                Spacer(Modifier.height(24.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    if (declineText != null)
                        TextButton(onClick = onAccept) {
                            Text(declineText)
                        }
                    TextButton(onClick = onDecline) {
                        Text(acceptText)
                    }
                }
            }
        }
    }
}