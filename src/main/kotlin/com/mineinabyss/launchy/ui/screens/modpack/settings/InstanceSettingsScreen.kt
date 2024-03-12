package com.mineinabyss.launchy.ui.screens.modpack.settings

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FileOpen
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.data.Constants.SETTINGS_HORIZONTAL_PADDING
import com.mineinabyss.launchy.data.modpacks.Group
import com.mineinabyss.launchy.data.modpacks.Mod
import com.mineinabyss.launchy.data.modpacks.ModConfig
import com.mineinabyss.launchy.logic.DesktopHelpers
import com.mineinabyss.launchy.logic.Instances.delete
import com.mineinabyss.launchy.logic.Instances.updateInstance
import com.mineinabyss.launchy.ui.elements.*
import com.mineinabyss.launchy.ui.screens.LocalModpackState
import com.mineinabyss.launchy.ui.screens.Screen
import com.mineinabyss.launchy.ui.screens.screen
import kotlin.io.path.listDirectoryEntries

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
fun InstanceProperties(
    minecraftDir: String,
    onChangeMinecraftDir: (String) -> Unit
) {
    var directoryPickerShown by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        DirectoryDialog(
            directoryPickerShown,
            title = "Choose your .minecraft directory",
            fallbackTitle = "Choose a file in your .minecraft directory",
            onCloseRequest = {
                if (it != null) onChangeMinecraftDir(it.toString())
                directoryPickerShown = false
            },
        )
        Column(Modifier.padding(start = 8.dp)) {
            OutlinedTextField(
                value = minecraftDir,
                singleLine = true,
                leadingIcon = { Icon(Icons.Rounded.Folder, contentDescription = "Directory") },
                trailingIcon = {
                    IconButton(onClick = { directoryPickerShown = true }) {
                        Icon(Icons.Rounded.FileOpen, contentDescription = "Choose")
                    }
                },
                onValueChange = { onChangeMinecraftDir(it) },
                label = { Text(".minecraft directory") },
                modifier = Modifier.fillMaxWidth()
            )
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
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { pack.instance.updateInstance(state) }) {
                    Text("Force update Instance")
                }
                OutlinedButton(onClick = { DesktopHelpers.openDirectory(pack.instance.minecraftDir) }) {
                    Text("Open .minecraft folder")
                }
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
        val userMods by remember {
            mutableStateOf(
                state.instance.userMods.listDirectoryEntries("*.jar").map {
                    Mod(
                        downloadDir = it,
                        modId = it.fileName.toString(),
                        info = ModConfig(name = it.fileName.toString()),
                        desiredHashes = null
                    )
                }
            )
        }
        Box(Modifier.padding(paddingValues)) {
            Box(Modifier.padding(horizontal = SETTINGS_HORIZONTAL_PADDING)) {
                val lazyListState = rememberLazyListState()
                LazyColumn(Modifier.fillMaxSize().padding(end = 12.dp), lazyListState) {
                    item { Spacer(Modifier.height(4.dp)) }
                    items(state.modpack.mods.modGroups.toList()) { (group, mods) ->
                        ModGroup(group, mods)
                    }
                    if (userMods.isNotEmpty()) item {
                        ModGroup(Group("User mods", forceEnabled = true), userMods)
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
