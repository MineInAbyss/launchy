package com.mineinabyss.launchy.ui.screens.modpack.main.buttons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Login
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.logic.Auth
import com.mineinabyss.launchy.ui.elements.PrimaryButton
import kotlinx.coroutines.launch

@Composable
fun AuthButton() {
    val state = LocalLaunchyState
    val coroutineScope = rememberCoroutineScope()

    PrimaryButton(
        enabled = state.profile.currentSession == null,
        onClick = {
            coroutineScope.launch {
                Auth.authOrShowDialog(state, state.profile)
            }
        },
    ) {
        Icon(Icons.AutoMirrored.Rounded.Login, "Login")
        Text("Login")
    }
}
