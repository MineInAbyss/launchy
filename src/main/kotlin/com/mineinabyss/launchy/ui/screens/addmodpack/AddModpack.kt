package com.mineinabyss.launchy.ui.screens.addmodpack

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.Numbers
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.ui.elements.ComfyContent
import com.mineinabyss.launchy.ui.elements.ComfyWidth

@Composable
fun NewInstance() {
    var urlText by remember { mutableStateOf("") }
    var selectedTabIndex by remember { mutableStateOf(0) }
    Column {
        ComfyWidth {
            PrimaryTabRow(selectedTabIndex = selectedTabIndex) {
                Tab(
                    text = {
                        Row {
//                        Icon(
//                            Icons.Rounded.Add, contentDescription = "Import"
//                        )
                            Text("Import")
                        }
                    },
                    selected = true,
                    onClick = { selectedTabIndex = 0 }
                )
                Tab(
                    text = { Text("Manual") },
                    selected = false,
                    onClick = { selectedTabIndex = 1 }
                )
            }
        }
        ComfyContent {
            AnimatedVisibility(visible = selectedTabIndex == 0, enter = fadeIn(), exit = fadeOut()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Import from link", style = MaterialTheme.typography.headlineMedium)
                    OutlinedTextField(
                        value = urlText,
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Rounded.Link, contentDescription = "Link") },
                        onValueChange = { urlText = it },
                        label = { Text("Link") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    TextButton(onClick = {}) {
                        Text("Import", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            AnimatedVisibility(visible = selectedTabIndex == 1, enter = fadeIn(), exit = fadeOut()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Create your own instance", style = MaterialTheme.typography.headlineMedium)
                    OutlinedTextField(
                        value = urlText,
                        singleLine = true,
                        onValueChange = { urlText = it },
                        label = { Text("Name") },
                        leadingIcon = { Icon(Icons.Rounded.TextFields, contentDescription = "Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = urlText,
                        singleLine = true,
                        onValueChange = { urlText = it },
                        label = { Text("Minecraft version") },
                        leadingIcon = { Icon(Icons.Rounded.Numbers, contentDescription = "Minecraft") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
