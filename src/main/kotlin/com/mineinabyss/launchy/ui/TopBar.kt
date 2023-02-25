package com.mineinabyss.launchy.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ManageAccounts
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SettingsApplications
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CropSquare
import androidx.compose.material.icons.rounded.Minimize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.ui.screens.Screen
import com.mineinabyss.launchy.ui.screens.screen
import com.mineinabyss.launchy.ui.state.TopBarState

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
fun TabIconButton(label: String = "", icon: ImageVector, onClick: () -> Unit) {
    Button(onClick, shape = RectangleShape, colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.background, disabledContainerColor = MaterialTheme.colorScheme.onPrimaryContainer)) {
        Icon(icon, "", tint = MaterialTheme.colorScheme.onPrimaryContainer)
        Text(label, color = MaterialTheme.colorScheme.onPrimaryContainer)
    }
}

@Composable
fun TabIconBar(
    state: TopBarState,
    transparent: Boolean,
    tabIconScreens: Set<Screen>,
    onTabIconClicked: (() -> Unit),
) = state.windowScope.WindowDraggableArea {
    Box(
        Modifier.fillMaxWidth().height(40.dp)
    ) {
        AnimatedVisibility(
            !transparent,
            enter = slideIn(initialOffset = { IntOffset(0, -120) }),
            exit = slideOut(targetOffset = { IntOffset(0, -120) })
        ) {
            Surface(tonalElevation = 1.dp, modifier = Modifier.fillMaxSize()) {}
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            Spacer(Modifier.width(5.dp))
            Row(
                Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                TabIconButton("Accounts", Icons.Outlined.ManageAccounts) { screen = Screen.Account }
                TabIconButton("Java", Icons.Outlined.SettingsApplications) { screen = Screen.Java }
                TabIconButton("Mods", Icons.Outlined.Settings) { screen = Screen.Mods }
            }
            Row {

            }
        }
    }
}

@Composable
fun AppTopBar(
    state: TopBarState,
    transparent: Boolean,
    showBackButton: Boolean,
    onBackButtonClicked: (() -> Unit),
) = state.windowScope.WindowDraggableArea {
    Box(
        Modifier.fillMaxWidth().height(40.dp)
    ) {
        AnimatedVisibility(
            !transparent,
            enter = slideIn(initialOffset = { IntOffset(0, -40) }),
            exit = slideOut(targetOffset = { IntOffset(0, -40) })
        ) {
            Surface(tonalElevation = 1.dp, modifier = Modifier.fillMaxSize()) {}
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            Spacer(Modifier.width(5.dp))
            Row(
                Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AnimatedVisibility(showBackButton/*, enter = fadeIn(animationSpec = tween(300, 300))*/) {
                    IconButton(onClick = onBackButtonClicked) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back button")
                    }
                    Spacer(Modifier.width(5.dp))
                }
                AnimatedVisibility(!transparent) {
                    Text(
                        "Mine in Abyss",
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Row {
                WindowButton(Icons.Rounded.Minimize) {
                    state.windowState.isMinimized = true
                }
                WindowButton(Icons.Rounded.CropSquare) {
                    state.toggleMaximized()
                }
                WindowButton(Icons.Rounded.Close) {
                    state.onClose()
                }
            }
        }
    }
}
