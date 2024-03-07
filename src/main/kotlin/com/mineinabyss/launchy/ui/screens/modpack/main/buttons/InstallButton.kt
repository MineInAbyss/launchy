package com.mineinabyss.launchy.ui.screens.modpack.main.buttons

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.logic.ModDownloader.install
import com.mineinabyss.launchy.ui.elements.PrimaryButton
import com.mineinabyss.launchy.ui.screens.LocalModpackState
import kotlinx.coroutines.launch

@Composable
fun InstallButton(enabled: Boolean, modifier: Modifier = Modifier) {
    val state = LocalLaunchyState
    val packState = LocalModpackState
    PrimaryButton(
        enabled = enabled,
        onClick = {
            state.downloadContext.launch {
                packState.install(state)
            }
        },
        modifier = modifier.width(150.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Download, "Download")
            val queued = packState.queued
            AnimatedVisibility(true, Modifier.animateContentSize()) {
                val isDownloading = packState.downloads.isDownloading
                InstallTextAnimatedVisibility(queued.areOperationsQueued && !isDownloading) {
                    Text("Install")
                }
                InstallTextAnimatedVisibility(!queued.areOperationsQueued && !isDownloading) {
                    Text("Installed")
                }
                InstallTextAnimatedVisibility(isDownloading) {
                    Text("Installing")
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
