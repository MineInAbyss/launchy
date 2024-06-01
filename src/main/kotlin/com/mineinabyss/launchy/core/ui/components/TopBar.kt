package com.mineinabyss.launchy.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CropSquare
import androidx.compose.material.icons.rounded.Minimize
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.core.ui.Constants
import com.mineinabyss.launchy.core.ui.TopBarState

@Composable
fun WindowButton(icon: ImageVector, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxHeight().width(44.dp),
        contentColor = Color.White,
        color = Color.Transparent
    ) {
        Icon(icon, "", Modifier.padding(10.dp))
    }
}

@Composable
fun AppTopBar(
    state: TopBarState,
    transparent: Boolean,
    showTitle: Boolean,
    showBackButton: Boolean,
    onBackButtonClicked: (() -> Unit),
) {
    val appState = LocalLaunchyState
    val forceFullscreen = appState.ui.fullscreen
    LaunchedEffect(forceFullscreen) {
        when (forceFullscreen) {
            true -> state.windowState.placement = WindowPlacement.Fullscreen
            false -> state.windowState.placement = WindowPlacement.Floating
        }
    }

    if (!forceFullscreen) state.windowScope.BetterWindowDraggableArea(
        Modifier.pointerInput(Unit) {
            detectTapGestures(onDoubleTap = {
                state.toggleMaximized()
            })
        }
    ) {
        Box(Modifier.fillMaxWidth().height(40.dp))
    }

    Box(
        Modifier.fillMaxWidth().height(40.dp)
    ) {
        AnimatedVisibility(
            !transparent,
            enter = slideIn(initialOffset = { IntOffset(0, -40) }),
            exit = slideOut(targetOffset = { IntOffset(0, -40) })
        ) {
            Surface(tonalElevation = 2.dp, modifier = Modifier.fillMaxSize()) {}
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            Row(
                Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AnimatedVisibility(showBackButton/*, enter = fadeIn(animationSpec = tween(300, 300))*/) {
                    WindowButton(Icons.AutoMirrored.Rounded.ArrowBack) {
                        onBackButtonClicked()
                    }
                    Spacer(Modifier.width(5.dp))
                }
                AnimatedVisibility(showTitle) {
                    Row {
                        Spacer(Modifier.width(8.dp))
                        LaunchyTitle()
                    }
                }
            }
            Row {
                AnimatedVisibility(!forceFullscreen) {
                    Row {
                        WindowButton(Icons.Rounded.Minimize) {
                            state.windowState.isMinimized = true
                        }
                        WindowButton(Icons.Rounded.CropSquare) {
                            state.toggleMaximized()
                        }
                    }
                }
                WindowButton(Icons.Rounded.Close) {
                    state.onClose()
                }
            }
        }
    }
}

@Composable
fun LaunchyTitle() {
    Row {
        Icon(
            Icons.Rounded.RocketLaunch,
            contentDescription = "Launchy",
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            "Launchy - ${Constants.APP_VERSION ?: "dev"}",
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun LaunchyHeadline() {
    Row {
        Icon(
            Icons.Rounded.RocketLaunch,
            contentDescription = "Launchy",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
        )
        Text(
            "Launchy",
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.headlineLarge
        )
    }
}
