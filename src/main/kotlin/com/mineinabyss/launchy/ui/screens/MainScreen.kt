package com.mineinabyss.launchy.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.WindowScope
import androidx.compose.ui.zIndex
import com.mineinabyss.launchy.AppTopBar
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.TopBar
import com.mineinabyss.launchy.data.Dirs
import com.mineinabyss.launchy.ui.ModGroup
import kotlinx.coroutines.launch
import kotlin.io.path.div
import kotlin.io.path.exists

sealed class Screen(val transparentTopBar: Boolean = false) {
    object Default : Screen(transparentTopBar = true)
    object Settings : Screen()
}

@Composable
fun Content() {
    var screen: Screen by remember { mutableStateOf(Screen.Default) }
    AnimatedVisibility(screen == Screen.Default, enter = fadeIn(), exit = fadeOut()) {
        MainScreen(TopBar.windowScope, onSettings = { screen = Screen.Settings })
    }
    Column {
        AnimatedVisibility(!screen.transparentTopBar, enter = fadeIn(), exit = fadeOut()) {
            Spacer(Modifier.height(40.dp))
        }
        Box {
            Animate(screen == Screen.Settings) {
                SettingsScreen()
            }
        }
    }

    AppTopBar(
        TopBar,
        screen.transparentTopBar,
        showBackButton = screen != Screen.Default,
        onBackButtonClicked = { screen = Screen.Default })
}


@Composable
fun Animate(enabled: Boolean, content: @Composable () -> Unit) {
    AnimatedVisibility(
        enabled,
        enter = fadeIn() + slideIn(initialOffset = { IntOffset(0, 100) }),
        exit = fadeOut() + slideOut(targetOffset = { IntOffset(0, 100) }),
    ) {
        content()
    }
}


@Composable
@Preview
fun SettingsScreen() {
    val state = LocalLaunchyState
    Scaffold(
        bottomBar = {
            InfoBar()
        },
    ) { paddingValues ->
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
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

@Composable
fun InfoText(shown: Boolean, icon: ImageVector, desc: String, extra: String = "") {
    if (shown) Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, desc)
        Text(desc, Modifier.padding(5.dp))
        Text(extra)
    }
}

@OptIn(ExperimentalUnitApi::class)
@Preview
@Composable
fun MainScreen(windowScope: WindowScope, onSettings: () -> Unit) {
    val colors = listOf(
        Color.Transparent,
        MaterialTheme.colorScheme.background,
    )
    val options = (Dirs.mineinabyss / "options.txt").toFile()
    var showPopup by remember {
        mutableStateOf(!options.exists() && Dirs.minecraft.exists())
    }

    Box {
        windowScope.WindowDraggableArea {
            Image(
                painter = painterResource("mia_render.jpg"),
                contentDescription = "Main render",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().then(
                    if (showPopup) {
                        Modifier.blur(10.dp)
                    } else Modifier
                )
            )
        }

        if (showPopup) {

            Surface(

                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.align(Alignment.Center).height(180.dp).width(400.dp).zIndex(5f),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                ) {
                    Text(
                        text = "Import Settings",
                        color = Color.LightGray,
                        textAlign = TextAlign.Start,
                        fontSize = TextUnit(24f, TextUnitType.Sp),
                        lineHeight = TextUnit(60f, TextUnitType.Sp)
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "This will import the options.txt file from your .minecraft directory.",
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontSize = TextUnit(16f, TextUnitType.Sp),
                        lineHeight = TextUnit(20f, TextUnitType.Sp)
                    )
                    Spacer(Modifier.height(20.dp))
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(
                            onClick = {
                                (Dirs.minecraft / "options.txt").toFile().copyTo(options)
                                showPopup = !showPopup
                            }
                        ) {
                            Text("Import")
                        }
                        TextButton(
                            onClick = { showPopup = !showPopup }
                        ) {
                            Text("Dont Import")
                        }
                    }
                }
            }
        }

        Column(
            modifier =
            Modifier.align(Alignment.Center)
                .heightIn(0.dp, 500.dp)
                .fillMaxSize()
                .zIndex(4f).then(
                    if (showPopup) Modifier.blur(10.dp)
                    else Modifier
                ),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource("mia_profile_icon.png"),
                contentDescription = "Mine in Abyss logo",
                modifier = Modifier.widthIn(0.dp, 500.dp).fillMaxSize().weight(3f),
                contentScale = ContentScale.FillWidth
            )
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth().weight(1f),
            ) {
                val state = LocalLaunchyState
                InstallButton(!state.isDownloading && state.operationsQueued && state.minecraftValid && !showPopup)
                Spacer(Modifier.width(10.dp))
                var toggled by remember { mutableStateOf(false) }
                AnimatedVisibility(state.operationsQueued) {
                    Button(
                        enabled = !showPopup,
                        onClick = { toggled = !toggled })
                    {
                        Column() {
                            Row {
                                Icon(Icons.Rounded.Update, contentDescription = "Updates")
                                Text("${state.queuedDownloads.size + state.queuedDeletions.size} Updates")
                            }

                            AnimatedVisibility(
                                toggled,
                                enter = expandIn(tween(200)) + fadeIn(tween(200, 100)),
                                exit = fadeOut() + shrinkOut(tween(200, 100))
                            ) {
                                Column {
                                    InfoText(
                                        shown = !state.fabricUpToDate,
                                        icon = Icons.Rounded.HistoryEdu,
                                        desc = "Install fabric",
                                    )
                                    InfoText(
                                        shown = state.updatesQueued,
                                        icon = Icons.Rounded.Update,
                                        desc = "Update",
                                        extra = state.queuedUpdates.size.toString()
                                    )
                                    InfoText(
                                        shown = state.installsQueued,
                                        icon = Icons.Rounded.Download,
                                        desc = "Download",
                                        extra = state.queuedInstalls.size.toString()
                                    )
                                    InfoText(
                                        shown = state.deletionsQueued,
                                        icon = Icons.Rounded.Delete,
                                        desc = "Remove",
                                        extra = state.queuedDeletions.size.toString()
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.width(10.dp))
//                NewsButton(hasUpdates = true)
//                Spacer(Modifier.width(10.dp))
                Button(
                    enabled = !showPopup,
                    onClick = onSettings
                ) {
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
fun NewsButton(hasUpdates: Boolean) {
    Box {
        Button(onClick = {}) {
            Icon(Icons.Rounded.Feed, contentDescription = "Settings")
            Text("News")
        }
        if (hasUpdates) Surface(
            Modifier.size(12.dp).align(Alignment.TopEnd).offset((-2).dp, (2).dp),
            shape = CircleShape,
            color = Color(255, 138, 128)
        ) {}
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
fun InstallButton(enabled: Boolean) {
    val state = LocalLaunchyState
    val coroutineScope = rememberCoroutineScope()
    Button(
        enabled = enabled,
        onClick = {
            coroutineScope.launch { state.install() }
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Icon(Icons.Rounded.Download, "Download")
        AnimatedVisibility(!state.minecraftValid) {
            Text("Invalid Minecraft")
        }
        AnimatedVisibility(state.minecraftValid) {
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
    }
}

@Composable
fun InfoBar(modifier: Modifier = Modifier) {
    val state = LocalLaunchyState
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
            InstallButton(!state.isDownloading && state.operationsQueued && state.minecraftValid)
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
