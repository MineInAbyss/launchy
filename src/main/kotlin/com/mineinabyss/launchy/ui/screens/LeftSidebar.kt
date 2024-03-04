package com.mineinabyss.launchy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.logic.Auth
import com.mineinabyss.launchy.ui.elements.PlayerAvatar
import kotlinx.coroutines.launch

@Composable
fun LeftSidebar() {
    val state = LocalLaunchyState
    val coroutineScope = rememberCoroutineScope()

    NavigationRail {
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
                    onClick = { }
                )
                NavigationRailItem(
                    icon = { Icon(Icons.Rounded.Add, contentDescription = "New instance") },
                    selected = screen == Screen.NewInstance,
                    onClick = { screen = Screen.NewInstance}
                )
                val profile = state.profile.currentProfile
                FloatingActionButton(
                    onClick = {
                        if (state.profile.currentSession == null) coroutineScope.launch {
                            if (profile == null) Auth.authOrShowDialog(state.profile)
                        }
                    },
                    modifier = Modifier.size(48.dp),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.secondary,
                ) {
                    profile?.let { PlayerAvatar(profile, Modifier.fillMaxSize()) }
//                                    ?: Icon(Icons.Rounded.Add, contentDescription = "Add account")
                }
            }
        }
    }
}
