package com.mineinabyss.launchy.ui.screens.home.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.data.Dirs
import com.mineinabyss.launchy.logic.DesktopHelpers
import com.mineinabyss.launchy.logic.SuggestedJVMArgs
import com.mineinabyss.launchy.ui.elements.*

@Composable
@Preview
fun SettingsScreen() {
    val state = LocalLaunchyState
    val scrollState = rememberScrollState()
    Column {
        ComfyTitle("Settings")
        ComfyContent {
            var directoryPickerShown by remember { mutableStateOf(false) }
            Column(
                Modifier.verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                Column {
                    TitleLarge("Interface")
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                state.ui.fullscreen,
                                onCheckedChange = { state.ui.fullscreen = it }
                            )
                            Text("Fullscreen mode")
                        }
                    }
                    Setting("Hue", icon = { Icon(Icons.Rounded.Colorize, contentDescription = "Hue") }) {
                        Slider(
                            value = state.ui.preferHue,
                            onValueChange = { state.ui.preferHue = it },
                            valueRange = 0f..1f,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Column {
                    TitleLarge("Quick access")
                    @OptIn(ExperimentalLayoutApi::class)
                    FlowRow(
                        maxItemsInEachRow = 2,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        OutlinedButton(onClick = { DesktopHelpers.openDirectory(Dirs.config) }) {
                            Text("Open launchy config dir")
                        }
                        OutlinedButton(onClick = { DesktopHelpers.openDirectory(Dirs.modpacksDir) }) {
                            Text("Open modpacks dir")
                        }
                    }
                }

                Column {
                    TitleLarge("Java")

                    SingleFileDialog(
                        directoryPickerShown,
                        title = "Choose java executable",
                        onCloseRequest = {
                            if (it != null) {
                                state.jvm.javaPath = it
                            }
                            directoryPickerShown = false
                        },
                        fileExtensions = { listOf("exe") },
                        fallbackFilter = { dir, name -> name == "java.exe" || name == "java" }
                    )

                    Setting("Java path") {
                        OutlinedTextField(
                            value = state.jvm.javaPath?.toString() ?: "No path selected",
                            readOnly = true,
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Rounded.Folder, contentDescription = "Link") },
                            trailingIcon = {
                                IconButton(onClick = { directoryPickerShown = true }) {
                                    Icon(Icons.Rounded.FileOpen, contentDescription = "Choose")
                                }
                            },
                            onValueChange = {},
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Setting("Memory", icon = { Icon(Icons.Rounded.Memory, "Memory icon") }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val memory = state.jvm.userMemoryAllocation ?: SuggestedJVMArgs.memory

                            Slider(
                                value = memory.toFloat(),
                                onValueChange = { state.jvm.userMemoryAllocation = it.toInt() },
                                valueRange = 1024f..8192f,
                                steps = 13,
                                modifier = Modifier.weight(1f)
                            )
                            TextField(
                                value = memory.toString(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                ),
                                onValueChange = { state.jvm.userMemoryAllocation = it.toIntOrNull() ?: memory },
                                label = { Text("Memory (MB)") },
                                modifier = Modifier.widthIn(120.dp)
                            )
                        }
                    }

                    Setting("JVM arguments") {
                        AnimatedVisibility(!state.jvm.useRecommendedJvmArgs) {
                            OutlinedTextField(
                                value = state.jvm.userJvmArgs ?: "",
                                enabled = !state.jvm.useRecommendedJvmArgs,
                                singleLine = false,
                                leadingIcon = { Icon(Icons.Rounded.Code, contentDescription = "") },
                                onValueChange = { state.jvm.userJvmArgs = it },
                                label = { Text("Custom JVM arguments") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                !state.jvm.useRecommendedJvmArgs,
                                onCheckedChange = { state.jvm.useRecommendedJvmArgs = !it })
                            Text("Use custom JVM arguments")
                        }

                        Spacer(Modifier.height(16.dp))

                        OutlinedTextField(
                            value = state.jvm.jvmArgs,
                            enabled = false,
                            singleLine = false,
                            readOnly = true,
                            leadingIcon = { Icon(Icons.Rounded.Code, contentDescription = "") },
                            onValueChange = { },
                            label = { Text("Full arguments") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

