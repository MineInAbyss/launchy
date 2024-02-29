package com.mineinabyss.launchy.ui.auth

import androidx.compose.material.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.window.WindowScope
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.data.auth.SessionStorage
import com.mineinabyss.launchy.logic.Auth
import com.mineinabyss.launchy.logic.Downloader
import com.mineinabyss.launchy.ui.elements.LaunchyDialog
import com.mineinabyss.launchy.ui.screens.settings.Browser
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
                state,
                onGetMSA = {
                    Browser.browse(it.verificationUri)
                    authMessage = it.message
                },
                onGetSession = {
                    coroutineScope.launch(Dispatchers.IO) {
                        SessionStorage.save(state, it)
                    }
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
