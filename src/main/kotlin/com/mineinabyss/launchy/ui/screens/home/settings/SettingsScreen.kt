package com.mineinabyss.launchy.ui.screens.home.settings

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.FileOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.AwtWindow
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.data.Dirs
import com.mineinabyss.launchy.logic.SuggestedJVMArgs
import com.mineinabyss.launchy.state.JvmState
import com.mineinabyss.launchy.ui.elements.ComfyContent
import com.mineinabyss.launchy.ui.elements.ComfyWidth
import com.mineinabyss.launchy.util.OS
import java.awt.FileDialog
import java.awt.Frame
import java.nio.file.Path
import kotlin.io.path.Path

@Composable
@Preview
fun SettingsScreen() {
    val state = LocalLaunchyState
    val scrollState = rememberScrollState()
    Column {
        ComfyWidth {
            Text("Settings", style = MaterialTheme.typography.headlineMedium)
        }
        ComfyContent {
            var directoryPickerShown by remember { mutableStateOf(false) }
            Column(Modifier.padding(16.dp).verticalScroll(scrollState), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Column {
                    Text("Hue", style = MaterialTheme.typography.titleSmall)
                    Row {
                        Slider(
                            value = state.preferHue,
                            onValueChange = { state.preferHue = it },
                            valueRange = 0f..1f,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                if (directoryPickerShown) FileDialog(onCloseRequest = {
                    if (it != null) {
                        state.jvm.javaPath = it
                    }
                    directoryPickerShown = false
                })
                Column {
                    Text("Java path", style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.jvm.javaPath?.toString() ?: "No path selected",
                        readOnly = true,
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Rounded.FileOpen, contentDescription = "Link") },
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth()
                    )

                    TextButton(onClick = { directoryPickerShown = true }) {
                        Text("Select Java Path", color = MaterialTheme.colorScheme.primary)
                    }
                }

                Column {
                    Text("Memory", style = MaterialTheme.typography.titleSmall)
                    Row {
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

                Column {
                    Text("JVM arguments", style = MaterialTheme.typography.titleSmall)
                    OutlinedTextField(
                        value = state.jvm.userJvmArgs ?: "",
                        enabled = !state.jvm.useRecommendedJvmArgs,
                        singleLine = false,
                        leadingIcon = { Icon(Icons.Rounded.Code, contentDescription = "") },
                        onValueChange = { state.jvm.userJvmArgs = it },
                        label = { Text("Override JVM arguments") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(state.jvm.useRecommendedJvmArgs, onCheckedChange = { state.jvm.useRecommendedJvmArgs = it })
                        Text("Use recommended JVM arguments")
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

@Composable
fun FileDialog(
    parent: Frame? = null,
    onCloseRequest: (result: Path?) -> Unit
) {
    when(OS.get()) {
        OS.WINDOWS -> FilePicker(
            true,
            initialDirectory = Dirs.jdks.toString(),
            title = "Choose java executable",
            fileExtensions = listOf("exe"),
        ) { file ->
            onCloseRequest(file?.let { Path(it.path) })
        }
        else -> AwtWindow(
            create = {
                object : FileDialog(parent, "Choose a file", LOAD) {
                    override fun setVisible(value: Boolean) {
                        super.setVisible(value)
                        if (value) {
                            onCloseRequest(files.firstOrNull()?.toPath())
                        }
                    }
                }.apply {
                    setFilenameFilter { dir, name ->
                        name == "java.exe" || name == "java"
                    }
                }
            },
            dispose = FileDialog::dispose
        )
    }

}
