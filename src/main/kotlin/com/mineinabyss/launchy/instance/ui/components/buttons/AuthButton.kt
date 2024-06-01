package com.mineinabyss.launchy.instance.ui.components.buttons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Login
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.auth.data.identity.IdentityDataSource
import com.mineinabyss.launchy.core.ui.components.PrimaryButton
import kotlinx.coroutines.launch

@Composable
fun AuthButton() {
    val state = LocalLaunchyState
    val coroutineScope = rememberCoroutineScope()

    PrimaryButton(
        enabled = state.profile.currentSession == null,
        onClick = {
            coroutineScope.launch {
                IdentityDataSource.authOrShowDialog(state, state.profile)
            }
        },
    ) {
        Icon(Icons.AutoMirrored.Rounded.Login, "Login")
        Text("Login")
    }
}
