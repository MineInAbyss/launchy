package com.mineinabyss.launchy.core.ui.components.topbar

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
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import com.mineinabyss.launchy.core.ui.LocalUiState
import com.mineinabyss.launchy.core.ui.TopBarState
import com.mineinabyss.launchy.core.ui.components.BetterWindowDraggableArea

@Composable
fun AppTopBar(
    state: TopBarState,
    transparent: Boolean,
    showTitle: Boolean,
    showBackButton: Boolean,
    onBackButtonClicked: (() -> Unit),
) {
    val ui = LocalUiState.current
    val forceFullscreen = ui.fullscreen
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

