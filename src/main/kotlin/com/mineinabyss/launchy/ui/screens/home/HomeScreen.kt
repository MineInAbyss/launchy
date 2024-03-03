package com.mineinabyss.launchy.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
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
fun HomeScreen() {
    val state = LocalLaunchyState
    val coroutineScope = rememberCoroutineScope()

    Scaffold { paddingValues ->
        Box(Modifier.padding(paddingValues)) {
            val profile = state.profile.currentProfile
            val fabSize = 48.dp
            val fabPadding = 10.dp

            Box(Modifier.padding(16.dp).fillMaxSize()) {
                ModpackGroup("Downloaded", state.downloadedModpacks)

                Column(
                    Modifier.padding(fabPadding).align(Alignment.BottomEnd),
                    verticalArrangement = Arrangement.spacedBy(fabPadding)
                ) {
                    FloatingActionButton(
                        onClick = {
                            coroutineScope.launch {
                                if (profile == null) Auth.authOrShowDialog(state.profile)
                            }
                        },
                        modifier = Modifier.size(fabSize),
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.secondary,
                    ) {
                        profile?.let { PlayerAvatar(profile, Modifier.fillMaxSize()) }
                            ?: Icon(Icons.Rounded.Add, contentDescription = "Add account")
                    }
                    FloatingActionButton(
                        onClick = { },
                        modifier = Modifier.size(fabSize),
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.secondary,
                    ) {
                        Icon(Icons.Rounded.Settings, contentDescription = "Add modpack")
                    }
                }
            }
        }
    }
}

