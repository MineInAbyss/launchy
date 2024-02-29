package com.mineinabyss.launchy.ui.auth

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Login
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowScope
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.data.auth.SessionStorage
import com.mineinabyss.launchy.logic.Auth
import com.mineinabyss.launchy.ui.elements.LaunchyDialog
import com.mineinabyss.launchy.ui.screens.settings.Browser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun AuthDialog(
    windowScope: WindowScope,
    onDismissRequest: () -> Unit,
    onComplete: () -> Unit,
) {
    val state = LocalLaunchyState
    // Start auth flow in coroutine
    var authMessage: String? by remember { mutableStateOf(null) }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            Auth.authFlow(
                state,
                onGetMSA = {
                    Browser.browse(it.verificationUri)
                    authMessage = it.message
                },
                onGetSession = {
                    SessionStorage.save(it)
                    state.currentSession = it
                    onComplete()
                }
            )
        }
    }
    LaunchyDialog(
        title = {
            Text("Authenticate with Microsoft", style = LocalTextStyle.current)
        },
        content = {
            when {
                authMessage != null -> {
                    Text(authMessage!!)
                }

                else -> Text("Getting authentiaction code...", style = LocalTextStyle.current)
            }
        },
        windowScope,
        onDismissRequest,
        onDismissRequest,
        "Cancel",
        null,
    )
}
