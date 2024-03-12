package com.mineinabyss.launchy.ui.screens.modpack.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.ui.elements.LaunchyDialog
import com.mineinabyss.launchy.ui.state.windowScope

@Composable
fun FirstLaunchDialog() {
    val state = LocalLaunchyState
    if (state.onboardingComplete) return

    val complete = { state.onboardingComplete = true}
    // Overlay that prevents clicking behind it
    windowScope.WindowDraggableArea {
        Box(Modifier.background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)).fillMaxSize())
    }

    LaunchyDialog(
        title = { Text("Welcome to Launchy!") },
        onAccept = complete,
        onDecline = complete,
        onDismiss = complete,
        acceptText = "Ok",
        declineText = null,
        content = {
            Text(
                """Launchy is a launcher & mod installer provided by the MineInAbyss team. 
                You can launch the game by connecting your Microsoft account. 
                It comes bundled with a bunch of recommended mods for performance and quality of life. 
                You can change these settings later in the settings screen.""".trimIndent(),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 10.dp),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    )
}
