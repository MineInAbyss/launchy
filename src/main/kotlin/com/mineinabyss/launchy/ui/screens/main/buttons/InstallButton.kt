package com.mineinabyss.launchy.ui.screens.main.buttons

import androidx.compose.animation.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mineinabyss.launchy.LocalLaunchyState
import kotlinx.coroutines.launch

@Composable
fun InstallButton(enabled: Boolean, modifier: Modifier = Modifier) {
    val state = LocalLaunchyState
    val coroutineScope = rememberCoroutineScope()
    Button(
        enabled = enabled,
        onClick = {
            coroutineScope.launch { state.install() }
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier
    ) {
        Icon(Icons.Rounded.Download, "Download")
        AnimatedVisibility(!state.minecraftValid) {
            Text("Invalid Minecraft")
        }
        AnimatedVisibility(state.minecraftValid, Modifier.animateContentSize()) {
            InstallTextAnimatedVisibility(state.operationsQueued && !state.isDownloading) {
                Text("Install")
            }
            InstallTextAnimatedVisibility(!state.operationsQueued && !state.isDownloading) {
                Text("Installed")
            }
            InstallTextAnimatedVisibility(state.isDownloading) {
                Text("Installing...")
            }
        }
    }
}

@Composable
fun InstallTextAnimatedVisibility(visible: Boolean, content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = shrinkHorizontally(shrinkTowards = Alignment.Start) + fadeOut()
    ) {
        content()
    }
}
