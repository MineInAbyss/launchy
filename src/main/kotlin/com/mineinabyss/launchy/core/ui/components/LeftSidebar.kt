package com.mineinabyss.launchy.core.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mineinabyss.launchy.auth.ui.ProfileViewModel
import com.mineinabyss.launchy.auth.ui.components.AccountsPopup
import com.mineinabyss.launchy.auth.ui.components.PlayerAvatar
import com.mineinabyss.launchy.core.ui.screens.Screen
import com.mineinabyss.launchy.core.ui.screens.screen

@Composable
fun LeftSidebar(
    profileViewModel: ProfileViewModel = viewModel(),
) {
    var showAccountsPopup by remember { mutableStateOf(false) }
    var accountHeadPosition: LayoutCoordinates? by remember { mutableStateOf(null) }

    NavigationRail(containerColor = Color.Transparent) {
        Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxHeight()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                NavigationRailItem(
                    icon = { Icon(Icons.Rounded.Home, contentDescription = "Home") },
                    selected = screen == Screen.Default,
                    onClick = { screen = Screen.Default }
                )
                NavigationRailItem(
                    icon = { Icon(Icons.Rounded.Settings, contentDescription = "Settings") },
                    selected = screen == Screen.Settings,
                    onClick = { screen = Screen.Settings }
                )
                NavigationRailItem(
                    icon = { Icon(Icons.Rounded.Add, contentDescription = "New instance") },
                    selected = screen == Screen.NewInstance,
                    onClick = {
                        screen = Screen.NewInstance
                    }
                )
                val profile by profileViewModel.profile.collectAsState()
                FloatingActionButton(
                    onClick = {
                        profileViewModel.authOrShowDialog()
                        if (profile == null) profileViewModel.authOrShowDialog()
                        else showAccountsPopup = !showAccountsPopup
                    },
                    modifier = Modifier.size(48.dp).onGloballyPositioned {
                        accountHeadPosition = it
                    },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.secondary,
                ) {
                    PlayerAvatar(profile, Modifier.fillMaxSize())
                }
            }
        }
    }
    accountHeadPosition?.let { position ->
        Box(Modifier.offset {
            val offset = position.positionInRoot()
            IntOffset(0, offset.y.toInt())
        }.padding(start = 80.dp)) {
            AnimatedVisibility(
                showAccountsPopup,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()
            ) {
                Row {
                    Spacer(Modifier.width(8.dp))
                    AccountsPopup(onLogout = { showAccountsPopup = false })
                }
            }
        }
    }
}
