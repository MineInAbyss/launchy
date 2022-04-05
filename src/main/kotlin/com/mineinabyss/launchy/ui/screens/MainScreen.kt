package com.mineinabyss.launchy.ui.screens

import androidx.compose.animation.*
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.ui.ModGroup
import kotlinx.coroutines.launch

@Composable
@Preview
fun MainScreen() {
    val state = LocalLaunchyState
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
//        bottomBar = {
//            InfoBar()
//        },
    ) { paddingValues ->
        var settingsOpen by remember { mutableStateOf(false) }
        println(settingsOpen)
        AbyssRender(onSettings = { settingsOpen = !settingsOpen })
        AnimatedVisibility(
            settingsOpen,
            enter = fadeIn() + slideIn(initialOffset = { IntOffset(0, 100) }),
            exit = fadeOut() + slideOut(targetOffset = { IntOffset(0, 100) }),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = { settingsOpen = !settingsOpen }) {
                    Icon(Icons.Rounded.ExpandMore, contentDescription = null)
                }
                Surface(
                    shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
                    modifier = Modifier.padding(5.dp)
                ) {
                    Box(
                        Modifier.padding(paddingValues)
                            .padding(start = 10.dp, top = 5.dp)
                    ) {
                        val lazyListState = rememberLazyListState()
                        LazyColumn(Modifier.fillMaxSize().padding(end = 12.dp), lazyListState) {

                            items(state.versions.modGroups.toList()) { (group, mods) ->
                                ModGroup(group, mods)
                            }
                        }
                        VerticalScrollbar(
                            modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd),
                            adapter = rememberScrollbarAdapter(lazyListState)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun AbyssRender(onSettings: () -> Unit) {
    val colors = listOf(
        Color.Transparent,
        MaterialTheme.colorScheme.background,
    )
    Box {
        Image(
            painter = painterResource("mia_render.jpg"),
            contentDescription = "Main render",
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            Modifier.align(Alignment.Center)
                .heightIn(0.dp, 500.dp)
                .fillMaxSize()
                .zIndex(4f),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource("mia_profile_icon.png"),
                contentDescription = "Mine in Abyss logo",
                modifier = Modifier
                    .widthIn(0.dp, 500.dp)
                    .fillMaxSize()
                    .weight(3f),
                contentScale = ContentScale.FillWidth
            )
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                Button(
                    onClick = {}, colors = ButtonDefaults.buttonColors(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.primary,
                    )
                ) {
                    Icon(Icons.Rounded.PlayArrow, contentDescription = "Play")
                    Text("Play")
                }
                Spacer(Modifier.width(10.dp))
                var toggled by remember { mutableStateOf(false) }
                Button(onClick = { toggled = !toggled }) {
                    Column() {
                        Row {
                            Icon(Icons.Rounded.Update, contentDescription = "Updates")
                            Text("10 Updates")
                        }
                        AnimatedVisibility(toggled) {
                            Column() {
                                Row {
                                    Icon(Icons.Rounded.Download, contentDescription = null)
                                    Text("5")
                                }
                                Row {
                                    Icon(Icons.Rounded.Update, contentDescription = null)
                                    Text("5")
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.width(10.dp))
                Box {
                    Button(onClick = {}) {
                        Icon(Icons.Rounded.Feed, contentDescription = "Settings")
                        Text("News")
                    }
                    Surface(
                        Modifier.size(12.dp).align(Alignment.TopEnd).offset((-2).dp, (2).dp),
                        shape = CircleShape,
                        color = Color(255, 138, 128)
                    ) {}
                }
                Spacer(Modifier.width(10.dp))
                Button(onClick = onSettings) {
                    Icon(Icons.Rounded.Settings, contentDescription = "Settings")
                    Text("Settings")
                }
            }
        }
        BoxWithConstraints(
            Modifier
                .align(Alignment.BottomCenter)
        ) {
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(maxHeight / 2)
                    .background(Brush.verticalGradient(colors))
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
