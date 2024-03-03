package com.mineinabyss.launchy.ui.screens.modpack.main.buttons

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.PlayDisabled
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.logic.Launcher
import com.mineinabyss.launchy.logic.ModDownloader.install
import com.mineinabyss.launchy.ui.elements.PrimaryButtonColors
import com.mineinabyss.launchy.ui.elements.SecondaryButtonColors
import com.mineinabyss.launchy.ui.screens.Dialog
import com.mineinabyss.launchy.ui.screens.LocalModpackState
import com.mineinabyss.launchy.ui.screens.dialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun PlayButton(
    hideText: Boolean = false,
) {
    val state = LocalLaunchyState
    val packState = LocalModpackState
    val process = state.processFor(packState)
    val coroutineScope = rememberCoroutineScope()
    val buttonSize = Size(120f, 0f)
    val buttonIcon by remember(state.profile.currentSession, process) {
        mutableStateOf(
            when {
                state.profile.currentProfile == null -> Icons.Rounded.PlayDisabled
                process == null -> Icons.Rounded.PlayArrow
                else -> Icons.Rounded.Stop
            }
        )
    }
    val buttonText by remember(process) {
        mutableStateOf(if (process == null) "Play" else "Stop")
    }
    val buttonColors by mutableStateOf(
        if (process == null) PrimaryButtonColors
        else SecondaryButtonColors
    )


    Box {
        Button(
            modifier = Modifier.width(with(LocalDensity.current) { buttonSize.width.toDp() }),
            enabled = state.profile.currentProfile != null && !packState.downloads.isDownloading,
            onClick = {
                if (process == null) {
                    if (packState.queued.downloads.isNotEmpty() || packState.queued.deletions.isNotEmpty())
                        dialog = Dialog.Options(
                            title = "Update before launch?",
                            message = "Updates are available for this modpack. Would you like to download them beofre launching?",
                            acceptText = "Download",
                            declineText = "Skip",
                            onAccept = {
                                coroutineScope.launch(Dispatchers.IO) {
                                    packState.install().join()
                                    Launcher.launch(state, packState, state.profile)
                                }
                            },
                            onDecline = {
                                coroutineScope.launch(Dispatchers.IO) {
                                    Launcher.launch(state, packState, state.profile)
                                }
                            }
                        )
                    else coroutineScope.launch(Dispatchers.IO) {
                        Launcher.launch(state, packState, state.profile)
                    }
                } else {
                    process.destroyForcibly()
                    state.launchedProcesses.remove(packState.packFolderName)
                }
            },
            shape = if (hideText) MaterialTheme.shapes.medium else RoundedCornerShape(20.dp),
            colors = buttonColors
        ) {
            Icon(buttonIcon, buttonText)
            if (!hideText) Text(buttonText)
        }
    }
}
