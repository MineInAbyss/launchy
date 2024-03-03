package com.mineinabyss.launchy.ui.screens.modpack.main.buttons

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.PlayDisabled
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.logic.Launcher
import com.mineinabyss.launchy.logic.ModDownloader.install
import com.mineinabyss.launchy.ui.elements.PrimaryButtonColors
import com.mineinabyss.launchy.ui.elements.SecondaryButtonColors
import com.mineinabyss.launchy.ui.screens.LocalModpackState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun PlayButton() {
    val state = LocalLaunchyState
    val packState = LocalModpackState
    val coroutineScope = rememberCoroutineScope()
    var showDropdown by rememberSaveable { mutableStateOf(false) }

    val buttonSize by remember(showDropdown) {
        mutableStateOf(Size(if (showDropdown) 200f else 120f, 0f))
    }
    val buttonIcon by remember(state.profile.currentSession, packState.currentLaunchProcess) {
        mutableStateOf(
            when {
                state.profile.currentProfile == null -> Icons.Rounded.PlayDisabled
                packState.currentLaunchProcess == null -> Icons.Rounded.PlayArrow
                else -> Icons.Rounded.Stop
            }
        )
    }
    val buttonText by remember(packState.currentLaunchProcess) {
        mutableStateOf(if (packState.currentLaunchProcess == null) "Play" else "Stop")
    }
    val buttonColors by mutableStateOf(
        if (packState.currentLaunchProcess == null) PrimaryButtonColors
        else SecondaryButtonColors
    )


    Box {
        DropdownMenu(
            modifier = Modifier.width(with(LocalDensity.current) { buttonSize.width.toDp() }),
            expanded = showDropdown,
            onDismissRequest = { showDropdown = false }) {
            DropdownMenuItem(
                contentPadding = PaddingValues(10.dp),
                enabled = showDropdown,
                text = { Text("Download updates & Launch") },
                onClick = {
                    showDropdown = false
                    coroutineScope.launch(Dispatchers.IO) {
                        packState.install()
                        Launcher.launch(packState, state.profile)
                    }
                })
            DropdownMenuItem(
                contentPadding = PaddingValues(10.dp),
                enabled = showDropdown,
                text = { Text("Launch without updating") },
                onClick = {
                    showDropdown = false
                    coroutineScope.launch(Dispatchers.IO) {
                        Launcher.launch(packState, state.profile)
                    }
                })
        }

        Button(
            modifier = Modifier.width(with(LocalDensity.current) { buttonSize.width.toDp() }),
            enabled = state.profile.currentProfile != null && !packState.downloads.isDownloading,
            onClick = {
                if (packState.currentLaunchProcess == null) {
                    if (packState.queued.downloads.isNotEmpty() || packState.queued.deletions.isNotEmpty()) showDropdown =
                        !showDropdown
                    else {
                        showDropdown = false
                        coroutineScope.launch(Dispatchers.IO) {
                            Launcher.launch(packState, state.profile)
                        }
                    }
                } else {
                    showDropdown = false
                    packState.currentLaunchProcess?.destroyForcibly()
                    packState.currentLaunchProcess = null
                }

            },
            shape = if (!showDropdown) RoundedCornerShape(20.dp) else MaterialTheme.shapes.medium,
            colors = buttonColors
        ) {
            Icon(buttonIcon, buttonText)
            Text(buttonText)
        }
    }
}
