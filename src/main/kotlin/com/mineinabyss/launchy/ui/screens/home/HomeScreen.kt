package com.mineinabyss.launchy.ui.screens.home

import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.LocalLaunchyState

@Composable
fun HomeScreen() {
    val state = LocalLaunchyState

    Box {
        val scrollState = rememberLazyListState()
        BoxWithConstraints {
            Column(Modifier.padding(end = 20.dp).fillMaxSize()) {
//                var searchQuery by remember { mutableStateOf("") }
//                SearchBar(
//                    searchQuery,
//                    active = false,
//                    placeholder = { Text("Search for modpacks") },
//                    onQueryChange = { searchQuery = it },
//                    onSearch = {},
//                    onActiveChange = {},
//                    modifier = Modifier.fillMaxWidth(),
//                    leadingIcon = {
//                        Icon(Icons.Rounded.Search, contentDescription = "Search")
//                    }
//                ) {
//                }
                LazyColumn(state = scrollState, modifier = Modifier.fillMaxSize()) {
                    item {
                        Spacer(Modifier.height(16.dp))
                    }
                    item {
                        InstanceList(
                            "Instances",
                            state.gameInstances.sortedByDescending { state.lastPlayed[it.config.name] })
                    }
//                    item {
//                        ModpackGroup("Find more", state.downloadedModpacks)
//                    }
                }
            }

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

