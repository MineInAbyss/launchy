package com.mineinabyss.launchy.ui.screens.main.buttons

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.logic.ModDownloader.install
import kotlinx.coroutines.launch

@Composable
fun InstallButton(enabled: Boolean, modifier: Modifier = Modifier) {
    val state = LocalLaunchyState
    val coroutineScope = rememberCoroutineScope()
    Button(
        enabled = enabled && state.modpackState != null,
        onClick = {
            coroutineScope.launch {
                state.modpackState?.install()
            }
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Download, "Download")
            val queued = state.modpackState?.queued ?: return@Row
            AnimatedVisibility(true, Modifier.animateContentSize()) {
                val isDownloading = !state.modpackState!!.downloads.isDownloading
                InstallTextAnimatedVisibility(queued.areOperationsQueued && isDownloading) {
                    Text("Install")
                }
                InstallTextAnimatedVisibility(!queued.areOperationsQueued && !isDownloading) {
                    Text("Installed")
                }
                InstallTextAnimatedVisibility(isDownloading) {
                    Text("Installing...")
                }
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
