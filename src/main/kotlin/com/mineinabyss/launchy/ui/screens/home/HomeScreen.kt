package com.mineinabyss.launchy.ui.screens.home

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.ui.elements.PlayerAvatar

@Composable
fun HomeScreen() {
    val state = LocalLaunchyState
    Scaffold { paddingValues ->
        Box(Modifier.padding(paddingValues)) {
            Box(Modifier.padding(12.dp)) {
                val lazyListState = rememberLazyListState()
                LazyColumn(Modifier.fillMaxSize().padding(end = 12.dp), lazyListState) {
                    item { Spacer(Modifier.height(4.dp)) }
                    items(state.downloadedModpacks.toList()) {
                        ModpackCard(it)
                    }
                }
                VerticalScrollbar(
                    modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd).padding(vertical = 2.dp),
                    adapter = rememberScrollbarAdapter(lazyListState)
                )
            }
            val profile = state.profile.currentProfile
            if (profile != null) {

                val fabSize = 64.dp

                FloatingActionButton(
                    onClick = { },
                    modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp).size(fabSize),
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary,
                ) {
                    PlayerAvatar(profile, Modifier.fillMaxSize())
                }
            }
        }
    }
}

