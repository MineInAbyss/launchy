package com.mineinabyss.launchy.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.ui.ModGroup
import kotlinx.coroutines.launch

@Composable
@Preview
fun MainScreen() {
    val state = LocalLaunchyState
    Scaffold(
        topBar = {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(2.dp)) {
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
                val coroutineScope = rememberCoroutineScope()
                Button(enabled = !state.isDownloading, onClick = {
                    coroutineScope.launch { state.downloadAndRemoveQueued() }
                }) {
                    AnimatedVisibility(!state.isDownloading) {
                        Text("Download mods")
                    }
                    AnimatedVisibility(state.isDownloading) {
                        Text("Downloading...")
                    }
                }
                Spacer(Modifier.width(10.dp))
//                AnimatedVisibility(state.isDownloading) {
                Text("Will download: ${state.queuedDownloads.size}")
                Spacer(Modifier.width(10.dp))
                Text("Will remove: ${state.queuedDeletions.size}")
//                }
            }
        }
    ) {
        Box {
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
