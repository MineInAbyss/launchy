package com.mineinabyss.launchy.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.HistoryEdu
import androidx.compose.material.icons.rounded.Update
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.ui.ModGroup
import kotlinx.coroutines.launch

@Composable
@Preview
fun MainScreen() {
    val state = LocalLaunchyState
    Scaffold(
        bottomBar = {
            BottomAppBar(backgroundColor = MaterialTheme.colors.surface) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(2.dp)
                ) {
                    val updatesQueued = state.queuedUpdates.isNotEmpty()
                    val installsQueued = state.queuedInstalls.isNotEmpty()
                    val deletionsQueued = state.queuedDeletions.isNotEmpty()
                    val operationsQueued = updatesQueued || installsQueued || deletionsQueued || !state.fabricUpToDate

                    val coroutineScope = rememberCoroutineScope()

                    Button(enabled = !state.isDownloading && operationsQueued, onClick = {
                        coroutineScope.launch { state.install() }
                    }) {
                        Icon(Icons.Rounded.Download, "Download")
                        AnimatedVisibility(!state.isDownloading) {
                            Text("Install")
                        }
                        AnimatedVisibility(state.isDownloading) {
                            Text("Installing...")
                        }
                    }
                    Spacer(Modifier.width(10.dp))

                    ActionButton(
                        shown = !state.fabricUpToDate,
                        icon = Icons.Rounded.HistoryEdu,
                        desc = "Will install fabric",
                    )
                    ActionButton(
                        shown = updatesQueued,
                        icon = Icons.Rounded.Update,
                        desc = "Will update",
                        extra = state.queuedUpdates.size.toString()
                    )
                    ActionButton(
                        shown = installsQueued,
                        icon = Icons.Rounded.Download,
                        desc = "Will download",
                        extra = state.queuedInstalls.size.toString()
                    )
                    ActionButton(
                        shown = deletionsQueued,
                        icon = Icons.Rounded.Delete,
                        desc = "Will remove",
                        extra = state.queuedDeletions.size.toString()
                    )

//                var path by remember { mutableStateOf("") }
//                Button(onClick = {
//                    path = FileDialog(ComposeWindow()).apply {
////                        setFilenameFilter { dir, name -> name.endsWith(".minecraft") }
//                        isVisible = true
//                    }.directory
//                }) {
//                    Text("File Picker")
//                }
//                Text(path)
                }
            }
        }
    ) { paddingValues ->
        Box(Modifier.padding(paddingValues)) {
            val lazyListState = rememberLazyListState()
            LazyColumn(Modifier.fillMaxSize().padding(end = 12.dp), lazyListState) {
                items(state.versions.modGroups.toList()) { (group, mods) ->
                    ModGroup(group, mods)
                }
            }
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(lazyListState)
            )
        }
    }
}

@Composable
fun ActionButton(shown: Boolean, icon: ImageVector, desc: String, extra: String = "") {
    AnimatedVisibility(shown) {
        var toggled by remember { mutableStateOf(false) }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { toggled = !toggled }) {
                Icon(icon, desc)
            }
            AnimatedVisibility(toggled) {
                Text(desc, Modifier.padding(end = 5.dp))
            }
            Text(extra)
        }
    }
}
