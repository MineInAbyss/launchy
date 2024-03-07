package com.mineinabyss.launchy.ui.screens.home.newinstance

import androidx.compose.foundation.layout.*
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.data.Dirs
import com.mineinabyss.launchy.data.config.GameInstance
import com.mineinabyss.launchy.data.config.GameInstanceConfig
import com.mineinabyss.launchy.logic.Downloader
import com.mineinabyss.launchy.logic.showDialogOnError
import com.mineinabyss.launchy.state.InProgressTask
import com.mineinabyss.launchy.ui.elements.AnimatedTab
import com.mineinabyss.launchy.ui.elements.ComfyContent
import com.mineinabyss.launchy.ui.elements.ComfyTitle
import com.mineinabyss.launchy.ui.elements.ComfyWidth
import com.mineinabyss.launchy.ui.screens.Screen
import com.mineinabyss.launchy.ui.screens.home.InstanceCard
import com.mineinabyss.launchy.ui.screens.screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.collections.set
import kotlin.io.path.deleteIfExists
import kotlin.io.path.div
import kotlin.io.path.exists

val validInstanceNameRegex = Regex("^[a-zA-Z0-9_ ]+$")

@Composable
fun NewInstance() {
    val state = LocalLaunchyState
    var selectedTabIndex by remember { mutableStateOf(0) }
    var importingInstance: GameInstanceConfig? by remember { mutableStateOf(null) }
    Column {
        ComfyWidth {
            PrimaryTabRow(selectedTabIndex = selectedTabIndex) {
                Tab(
                    text = { Text("Import") },
                    selected = true,
                    onClick = { selectedTabIndex = 0 }
                )
//                Tab(
//                    text = { Text("Manual") },
//                    selected = false,
//                    onClick = { selectedTabIndex = 1 }
//                )
            }
        }
        val coroutineScope = rememberCoroutineScope()
        Box {
            AnimatedTab(visible = selectedTabIndex == 0 && importingInstance == null) {
                Column {
                    ComfyTitle("Import from link")

                    ComfyContent {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            var urlText by remember { mutableStateOf("") }
                            var urlValid by remember { mutableStateOf(true) }
                            fun urlValid() = urlText.startsWith("https://") || urlText.startsWith("http://")
                            var urlFailedToParse by remember { mutableStateOf(false) }

                            OutlinedTextField(
                                value = urlText,
                                singleLine = true,
                                isError = !urlValid || urlFailedToParse,
                                leadingIcon = { Icon(Icons.Rounded.Link, contentDescription = "Link") },
                                onValueChange = {
                                    urlText = it
                                    urlFailedToParse = false
                                },
                                label = { Text("Link") },
                                supportingText = {
                                    if (!urlValid) Text("Must be valid URL")
                                    else if (urlFailedToParse) Text("URL is not a valid instance file")
                                },
                                modifier = Modifier.fillMaxWidth()
                            )

                            TextButton(onClick = {
                                urlValid = urlValid()
                                if (!urlValid) return@TextButton
                                val taskKey = "import-cloud-instance-${urlText.hashCode()}"
                                val downloadPath = Dirs.tmp / "launchy-cloud-instance-${urlText.hashCode()}.yml"
                                downloadPath.deleteIfExists()
                                coroutineScope.launch(Dispatchers.IO) {
                                    state.inProgressTasks[taskKey] = InProgressTask("Importing cloud instance")
                                    val cloudInstance = Downloader.download(urlText, downloadPath).mapCatching {
                                        GameInstanceConfig.read(downloadPath)
                                            .showDialogOnError("Failed to read cloud instance")
                                            .getOrThrow()
                                    }.getOrElse {
                                        urlFailedToParse = true
                                        state.inProgressTasks.remove(taskKey)
                                        return@launch
                                    }
                                    importingInstance = cloudInstance.copy(
                                        cloudInstanceURL = urlText
                                    )
                                    state.inProgressTasks.remove(taskKey)
                                }
                            }) {
                                Text("Import", color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
            AnimatedTab(importingInstance != null) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ComfyWidth {
                        Text("Confirm import", style = MaterialTheme.typography.headlineMedium)
                    }
                    ComfyContent {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            var nameText by remember { mutableStateOf(importingInstance?.name ?: "") }
                            fun nameValid() = nameText.matches(validInstanceNameRegex)
                            fun packWithNameExists() = Dirs.modpackConfigDir(nameText).exists()
                            var nameValid by remember { mutableStateOf(nameValid()) }
                            var packWithNameExists by remember { mutableStateOf(packWithNameExists()) }

                            OutlinedTextField(
                                value = nameText,
                                singleLine = true,
                                isError = !nameValid || packWithNameExists,
                                leadingIcon = { Icon(Icons.Rounded.TextFields, contentDescription = "Name") },
                                supportingText = {
                                    if (!nameValid) Text("Name must be alphanumeric")
                                    else if (packWithNameExists) Text("A modpack with this name already exists")
                                },
                                onValueChange = {
                                    nameText = it
                                    packWithNameExists = false
                                },
                                label = { Text("Instance name") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            TextButton(
                                enabled = importingInstance != null,
                                onClick = {
                                    nameValid = nameValid()
                                    packWithNameExists = packWithNameExists()
                                    val instance = importingInstance ?: return@TextButton
                                    if (!nameValid || packWithNameExists) return@TextButton
                                    GameInstance.create(
                                        state, instance.copy(
                                            name = nameText,
                                        )
                                    )
                                    screen = Screen.Default
                                }
                            ) {
                                Text("Confirm", color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }

                    ComfyWidth {
                        importingInstance?.let {
                            InstanceCard(
                                it.copy(name = "Preview"),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
//            AnimatedVisibility(visible = selectedTabIndex == 1, enter = fadeIn(), exit = fadeOut()) {
//                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
//                    Text("Create your own instance", style = MaterialTheme.typography.headlineMedium)
////                    OutlinedTextField(
////                        value = urlText,
////                        singleLine = true,
////                        onValueChange = { urlText = it },
////                        label = { Text("Name") },
////                        leadingIcon = { Icon(Icons.Rounded.TextFields, contentDescription = "Name") },
////                        modifier = Modifier.fillMaxWidth()
////                    )
////                    OutlinedTextField(
////                        value = urlText,
////                        singleLine = true,
////                        onValueChange = { urlText = it },
////                        label = { Text("Minecraft version") },
////                        leadingIcon = { Icon(Icons.Rounded.Numbers, contentDescription = "Minecraft") },
////                        modifier = Modifier.fillMaxWidth()
////                    )
//                }
//            }
    }
}
