package com.mineinabyss.launchy.ui.screens.main.buttons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Login
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.ui.auth.AuthDialog
import com.mineinabyss.launchy.ui.screens.Dialog
import com.mineinabyss.launchy.ui.screens.dialog
import com.mineinabyss.launchy.ui.state.windowScope

@Composable
fun AuthButton() {
    val state = LocalLaunchyState

    Button(
        enabled = state.currentSession == null,
        onClick = { dialog = Dialog.Auth },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Icon(Icons.AutoMirrored.Rounded.Login, "Login")
        Text("Login")
    }
}
