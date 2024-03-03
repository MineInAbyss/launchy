package com.mineinabyss.launchy.ui.screens.home

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.data.modpacks.ModpackInfo
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
                        modifier = Modifier.size(fabSize)
                            .border(1.dp, MaterialTheme.colorScheme.secondary, FloatingActionButtonDefaults.shape),
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
    Column {
        Text(title, style = MaterialTheme.typography.headlineLarge)
        BoxWithConstraints {
            val total = packs.size + 1
            val colums = ((maxWidth / ModpackCardStyle.cardWidth).toInt()).coerceAtMost(total).coerceAtLeast(1)
            LazyVerticalGrid(
                columns = GridCells.Fixed(colums),
                modifier = Modifier.width((16.dp + ModpackCardStyle.cardWidth) * total),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(packs) { pack ->
                    ModpackCard(pack)
                }
                item {
                    AddNewModpackCard()
                }
            }
        }
    }
}
