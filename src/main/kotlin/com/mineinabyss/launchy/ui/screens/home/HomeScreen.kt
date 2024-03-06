package com.mineinabyss.launchy.ui.screens.home

import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FileUpload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.ui.state.windowScope
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.*
import java.io.File

@Composable
fun HomeScreen() {
    val state = LocalLaunchyState

    Scaffold { paddingValues ->
        val scrollState = rememberLazyListState()
        BoxWithConstraints {
            val showDragFileCard = remember { mutableStateOf(false) }
            HandleFileDropping(showDragFileCard)
            ShowDropFileCard(showDragFileCard)
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
                        ModpackGroup("Instances", state.gameInstances)
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

@Composable
private fun HandleFileDropping(showDragFileCard: MutableState<Boolean>) {
    val state = LocalLaunchyState
    val target = object : DropTarget() {
        @Synchronized
        override fun dragEnter(event: DropTargetDragEvent) {
            val files = (event.transferable.getTransferData(DataFlavor.javaFileListFlavor) as List<*>).filterIsInstance<File>()
            if (files.any { it.extension == "mrpack" })
                showDragFileCard.value = true
            else event.rejectDrag()
        }

        @Synchronized
        override fun dragExit(event: DropTargetEvent) {
            showDragFileCard.value = false
        }

        @Synchronized
        override fun drop(event: DropTargetDropEvent) {
            showDragFileCard.value = false
            runCatching {
                event.acceptDrop(DnDConstants.ACTION_REFERENCE)
                val files = (event.transferable.getTransferData(DataFlavor.javaFileListFlavor) as List<*>).filterIsInstance<File>()
                files.firstOrNull { it.extension == "mrpack" }?.let {
                    //TODO This does not work as GameInstanceConfig#read doesnt properly deserialize .mrpack
                    //GameInstance.create(state, GameInstanceConfig.read(it.toPath()))
                }
            }.onFailure {
                it.printStackTrace()
            }
        }
    }
    windowScope.window.dropTarget = target
}

@Composable
private fun ShowDropFileCard(showDragFileCard: MutableState<Boolean>) {
    if (!showDragFileCard.value) return
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background.copy(alpha = 0.5f)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Row {
                Icon(Icons.Rounded.FileUpload, contentDescription = "Upload")
                Text("Drop a .mrpack to create an instance...", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
