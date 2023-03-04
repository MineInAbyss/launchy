package com.mineinabyss.launchy.ui.screens.main.buttons

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.data.Constants
import com.mineinabyss.launchy.ui.screens.main.showComingSoonDialog

@Composable
fun PlayButton(enabled: Boolean) {
    val state = LocalLaunchyState

    Button(
        enabled = Constants.ENABLE_PLAY_BUTTON && enabled,
        onClick = {
            showComingSoonDialog.value = true
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Icon(Icons.Rounded.PlayArrow, "Play")
        AnimatedVisibility(!state.minecraftValid) {
            Text("Invalid Minecraft")
        }
        AnimatedVisibility(state.minecraftValid) {
            Text("Play")
        }
    }
}
