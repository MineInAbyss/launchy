package com.mineinabyss.launchy.ui.screens.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.data.modpacks.ModpackInfo
import com.mineinabyss.launchy.logic.Auth
import com.mineinabyss.launchy.logic.Launcher
import com.mineinabyss.launchy.state.ProfileState
import com.mineinabyss.launchy.ui.elements.PlayerAvatar
import com.mineinabyss.launchy.ui.screens.LocalModpackState
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

            Box(Modifier.padding(12.dp).fillMaxSize()) {
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
                        modifier = Modifier.size(fabSize).border(1.dp, MaterialTheme.colorScheme.secondary, FloatingActionButtonDefaults.shape),
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.secondary,
                    ) {
                        profile?.let { PlayerAvatar(profile, Modifier.fillMaxSize()) }
                            ?: Icon(Icons.Rounded.Add, contentDescription = "Add account")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ModpackGroup(title: String, packs: List<ModpackInfo>) {
    Box(Modifier.height(312.dp)) {
        Column {
            Text(title, style = MaterialTheme.typography.headlineLarge)
            FlowRow(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                packs.forEach { pack -> ModpackCard(pack) }
                AddNewModpackCard()
            }
        }
    }
}
