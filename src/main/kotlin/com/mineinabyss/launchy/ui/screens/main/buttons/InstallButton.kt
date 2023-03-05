package com.mineinabyss.launchy.ui.screens.main.buttons

import androidx.compose.animation.*
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
        AnimatedVisibility(state.minecraftValid) {
            AnimatedVisibility(state.operationsQueued && !state.isDownloading) {
                Text("Install")
            }
            AnimatedVisibility(!state.operationsQueued && !state.isDownloading) {
                Text("Installed")
            }
            AnimatedVisibility(state.isDownloading) {
                Text("Installing...")
            }
        }
    }
}
