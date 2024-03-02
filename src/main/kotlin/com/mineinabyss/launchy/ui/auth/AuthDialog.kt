package com.mineinabyss.launchy.ui.auth

import androidx.compose.material.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.window.WindowScope
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.data.auth.SessionStorage
import com.mineinabyss.launchy.logic.Auth
import com.mineinabyss.launchy.ui.elements.LaunchyDialog
import com.mineinabyss.launchy.ui.screens.Dialog
import com.mineinabyss.launchy.ui.screens.dialog
import com.mineinabyss.launchy.logic.Browser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            Auth.authFlow(
                state.profile,
                onVerificationRequired = {
                    Browser.browse(it.verificationUri)
                    authMessage = it.message
                },
                onAuthenticate = {
                    coroutineScope.launch(Dispatchers.IO) {
                        SessionStorage.save(state, it)
                    }
                    state.profile.currentSession = it
                    dialog = Dialog.None
                    onComplete()
                }
            )
        }
    }
    if (authMessage == null) LaunchyDialog(
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
        { dialog = Dialog.None; onDismissRequest() },
        { dialog = Dialog.None; onDismissRequest() },
        "Cancel",
        null,
    )
}
