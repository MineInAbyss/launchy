package com.mineinabyss.launchy.instance_list.ui

import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mineinabyss.launchy.instance_list.ui.components.InstanceList

@Composable
fun HomeScreen(viewModel: InstanceListViewModel = viewModel()) {
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
                        val instances by viewModel.gameInstances.collectAsState()
                        val lastPlayed by viewModel.lastPlayed.collectAsState()
                        InstanceList(
                            "Instances",
                            instances.sortedByDescending { lastPlayed[it.config.name] }
                        )
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

