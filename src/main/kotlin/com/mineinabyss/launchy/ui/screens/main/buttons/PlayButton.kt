package com.mineinabyss.launchy.ui.screens.main.buttons

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.PlayDisabled
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.logic.Launcher
import com.mineinabyss.launchy.logic.mutableStateSetOf
import kotlinx.coroutines.launch

@Composable
fun PlayButton() {
    val state = LocalLaunchyState
    val coroutineScope = rememberCoroutineScope()
    var showDropdown by rememberSaveable { mutableStateOf(false) }
    var buttonSize by remember { mutableStateOf(Size.Unspecified) }
    var buttonIcon by remember { mutableStateOf(Icons.Rounded.PlayArrow) }
    var buttonText by remember { mutableStateOf("Play") }
    val buttonColors = if (state.currentLaunchProcess == null)
        ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.primary)
    else ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)

    buttonSize = Size(if (showDropdown) 200f else 120f, 0f)
    buttonIcon = when {
        state.currentSession == null -> Icons.Rounded.PlayDisabled
        state.currentLaunchProcess == null -> Icons.Rounded.PlayArrow
        else -> Icons.Rounded.Stop
    }
    buttonText = if (state.currentLaunchProcess == null) "Play" else "Stop"

    Box {
        DropdownMenu(modifier = Modifier.width(with(LocalDensity.current){buttonSize.width.toDp()}),expanded = showDropdown, onDismissRequest = { showDropdown = false }) {
            DropdownMenuItem(contentPadding = PaddingValues(10.dp), enabled = showDropdown, text = { Text("Download updates & Launch") }, onClick = {
                showDropdown = false
                coroutineScope.launch {
                    state.install()
                    Launcher.launch(state)
                }
            })
            DropdownMenuItem(contentPadding = PaddingValues(10.dp), enabled = showDropdown, text = { Text("Launch without updating") }, onClick = {
                showDropdown = false
                Launcher.launch(state)
            })

        }

        Button(
            modifier = Modifier.width(with(LocalDensity.current){buttonSize.width.toDp()}),
            enabled =  state.currentSession != null && state.currentModpack != null && !state.isDownloading,
            onClick = {
                if (state.currentLaunchProcess == null) {
                    if (state.queuedDownloads.isNotEmpty() || state.queuedDeletions.isNotEmpty()) showDropdown = !showDropdown
                    else {
                        showDropdown = false
                        Launcher.launch(state)
                    }
                } else {
                    showDropdown = false
                    state.currentLaunchProcess?.destroyForcibly()
                    state.currentLaunchProcess = null
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


