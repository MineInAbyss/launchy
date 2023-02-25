package com.mineinabyss.launchy.ui.screens.main.buttons

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Login
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.mineinabyss.launchy.LocalLaunchyState

@Composable
fun LoginMicrosoftButton(enabled: Boolean) {
    val state = LocalLaunchyState
    val coroutineScope = rememberCoroutineScope()

    Button(
        enabled = enabled,
        onClick = { startMicrosoftLoginProcess() },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.primary
        ),
    ) {
        Icon(Icons.Rounded.Login, "Login with Microsoft")
        AnimatedVisibility(!state.minecraftValid) {
            Text("Login with Microsoft")
        }
        AnimatedVisibility(state.minecraftValid) {
            Text("Login with Microsoft")
        }
    }
}

private fun startMicrosoftLoginProcess() {

}

private fun microsoftOAuth2Flow() {

}
