package com.mineinabyss.launchy.ui.screens.main.buttons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.logic.Launcher

@Composable
fun PlayButton() {
    val state = LocalLaunchyState

    Button(
        enabled = state.currentSession != null && state.currentModpack != null,
        onClick = {
            Launcher.launch(state)
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Icon(Icons.Rounded.PlayArrow, "Play")
        Text("Play")
    }
}
