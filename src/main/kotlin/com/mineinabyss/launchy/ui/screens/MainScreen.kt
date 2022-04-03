package com.mineinabyss.launchy.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.WindowScope
import androidx.compose.ui.window.WindowState
import com.mineinabyss.launchy.AppWindowTitleBar
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.ui.ModGroup
import kotlinx.coroutines.launch

@Composable
@Preview
fun MainScreen() {
    val state = LocalLaunchyState
    println("Updating!")
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        bottomBar = {
            InfoBar()
        },
    ) { paddingValues ->
        Box(Modifier.padding(paddingValues).padding(start = 10.dp, top = 5.dp)) {
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

@Composable
fun InfoBar(modifier: Modifier = Modifier) {
    val state = LocalLaunchyState

    val coroutineScope = rememberCoroutineScope()
    Surface(
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
//            BottomAppBar(backgroundColor = MaterialTheme.colorScheme.surface) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(6.dp)
        ) {
            println(state.operationsQueued && state.minecraftValid)
            Button(
                enabled = !state.isDownloading && state.operationsQueued && state.minecraftValid,
                onClick = {
                    coroutineScope.launch { state.install() }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Rounded.Download, "Download")
                AnimatedVisibility(state.operationsQueued && !state.isDownloading) {
                    Text("Install")
                }
                AnimatedVisibility(!state.operationsQueued) {
                    Text("Installed")
                }
                AnimatedVisibility(state.isDownloading) {
                    Text("Installing...")
                }
            }
            Spacer(Modifier.width(10.dp))

            ActionButton(
                shown = !state.minecraftValid,
                icon = Icons.Rounded.Error,
                desc = "No minecraft installation found",
            )

            ActionButton(
                shown = !state.fabricUpToDate,
                icon = Icons.Rounded.HistoryEdu,
                desc = "Will install fabric",
            )
            ActionButton(
                shown = state.updatesQueued,
                icon = Icons.Rounded.Update,
                desc = "Will update",
                extra = state.queuedUpdates.size.toString()
            )
            ActionButton(
                shown = state.installsQueued,
                icon = Icons.Rounded.Download,
                desc = "Will download",
                extra = state.queuedInstalls.size.toString()
            )
            ActionButton(
                shown = state.deletionsQueued,
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
//            }
    }
}
