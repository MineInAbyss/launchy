package com.mineinabyss.launchy.ui.screens.modpack.settings

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.data.Constants.SETTINGS_HORIZONTAL_PADDING
import com.mineinabyss.launchy.logic.Instances.delete
import com.mineinabyss.launchy.logic.Instances.updateInstance
import com.mineinabyss.launchy.ui.elements.AnimatedTab
import com.mineinabyss.launchy.ui.elements.ComfyContent
import com.mineinabyss.launchy.ui.elements.ComfyWidth
import com.mineinabyss.launchy.ui.elements.TitleSmall
import com.mineinabyss.launchy.ui.screens.LocalModpackState
import com.mineinabyss.launchy.ui.screens.Screen
import com.mineinabyss.launchy.ui.screens.screen

@Composable
@Preview
fun InstanceSettingsScreen() {
    val state = LocalModpackState
    var selectedTabIndex by remember { mutableStateOf(0) }
    ComfyWidth {
        Column {
            PrimaryTabRow(selectedTabIndex = selectedTabIndex, containerColor = Color.Transparent) {
                Tab(
                    text = { Text("Manage Mods") },
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 }
                )
                Tab(
                    text = { Text("Options") },
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 }
                )
            }
            Box(Modifier.fillMaxSize()) {
                AnimatedTab(selectedTabIndex == 0) {
                    ModManagement()
                }
                AnimatedTab(selectedTabIndex == 1) {
                    OptionsTab()
                }
            }
        }
    }
}

@Composable
fun OptionsTab() {
    val state = LocalLaunchyState
    val pack = LocalModpackState

    ComfyContent(Modifier.padding(16.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            TitleSmall("Mods")
            OutlinedButton(onClick = { pack.instance.updateInstance(state) }) {
                Text("Force update Instance")
            }

            TitleSmall("Danger zone")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = {
                    screen = Screen.Default
                    pack.instance.delete(state, deleteDotMinecraft = false)
                }) {
                    Text("Delete Instance from config")
                }
                OutlinedButton(
                    onClick = {
                        screen = Screen.Default
                        pack.instance.delete(state, deleteDotMinecraft = true)
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    )
                ) {
                    Text("Delete Instance and its .minecraft")
                }
            }
        }
    }
}

@Composable
fun ModManagement() {
    val state = LocalModpackState
    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = { InfoBar() },
    ) { paddingValues ->
        Box(Modifier.padding(paddingValues)) {
            Box(Modifier.padding(horizontal = SETTINGS_HORIZONTAL_PADDING)) {
                val lazyListState = rememberLazyListState()
                LazyColumn(Modifier.fillMaxSize().padding(end = 12.dp), lazyListState) {
                    item { Spacer(Modifier.height(4.dp)) }
                    items(state.modpack.mods.modGroups.toList()) { (group, mods) ->
                        ModGroup(group, mods)
                    }
                }
                VerticalScrollbar(
                    modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd).padding(vertical = 2.dp),
                    adapter = rememberScrollbarAdapter(lazyListState)
                )
            }
        }
    }
}