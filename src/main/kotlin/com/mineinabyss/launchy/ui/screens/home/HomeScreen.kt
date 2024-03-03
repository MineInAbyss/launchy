package com.mineinabyss.launchy.ui.screens.home

import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.logic.Auth
import com.mineinabyss.launchy.ui.elements.PlayerAvatar
import com.mineinabyss.launchy.ui.screens.Dialog
import com.mineinabyss.launchy.ui.screens.dialog
import kotlinx.coroutines.launch

@Composable
fun HomeScreen() {
    val state = LocalLaunchyState
    val coroutineScope = rememberCoroutineScope()

    Scaffold { paddingValues ->
        BoxWithConstraints(Modifier.padding(paddingValues).padding(top = 40.dp)) {
            NavigationRail {
                Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxHeight()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        NavigationRailItem(
                            icon = { Icon(Icons.Rounded.Home, contentDescription = "Home") },
//                            label = { Text("Home") },
                            selected = true,
                            onClick = { }
                        )
                        NavigationRailItem(
                            icon = { Icon(Icons.Rounded.Settings, contentDescription = "Settings") },
//                            label = { Text("Settings") },
                            selected = false,
                            onClick = { }
                        )
                        NavigationRailItem(
                            icon = { Icon(Icons.Rounded.Download, contentDescription = "Download pack") },
//                            label = { Text("Download") },
                            selected = false,
                            onClick = { dialog = Dialog.AddModpack }
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

            Column(Modifier.padding(start = 80.dp, end = 20.dp).fillMaxSize()) {
                var searchQuery by remember { mutableStateOf("") }
                SearchBar(
                    searchQuery,
                    active = false,
                    placeholder = { Text("Search for modpacks") },
                    onQueryChange = { searchQuery = it },
                    onSearch = {},
                    onActiveChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Rounded.Search, contentDescription = "Search")
                    }
                ) {
                }
                ModpackGroup("Downloaded", state.downloadedModpacks)
            }

            val scrollState = rememberScrollState()

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(20.dp).align(Alignment.CenterEnd)
            ) {
                VerticalScrollbar(
                    modifier = Modifier.fillMaxHeight(),
                    adapter = rememberScrollbarAdapter(scrollState),
                    style = LocalScrollbarStyle.current.copy(
                        unhoverColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
                        hoverColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}

