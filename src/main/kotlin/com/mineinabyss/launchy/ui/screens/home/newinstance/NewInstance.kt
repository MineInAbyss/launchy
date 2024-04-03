package com.mineinabyss.launchy.ui.screens.home.newinstance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.mineinabyss.launchy.logic.AppDispatchers
import com.mineinabyss.launchy.logic.Downloader
import com.mineinabyss.launchy.logic.showDialogOnError
import com.mineinabyss.launchy.state.InProgressTask
import com.mineinabyss.launchy.ui.elements.AnimatedTab
import com.mineinabyss.launchy.ui.elements.ComfyContent
import com.mineinabyss.launchy.ui.elements.ComfyTitle
import com.mineinabyss.launchy.ui.elements.ComfyWidth
import com.mineinabyss.launchy.ui.screens.Screen
import com.mineinabyss.launchy.ui.screens.home.InstanceCard
import com.mineinabyss.launchy.ui.screens.modpack.settings.InstanceProperties
import com.mineinabyss.launchy.ui.screens.screen
import kotlinx.coroutines.launch
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists

val validInstanceNameRegex = Regex("^[a-zA-Z0-9_ ]+$")

@Composable
fun NewInstance() {
    val state = LocalLaunchyState
    var selectedTabIndex by remember { mutableStateOf(0) }
    var importingInstance: GameInstance.CloudInstanceWithHeaders? by remember { mutableStateOf(null) }
    Column {
        ComfyWidth {
            PrimaryTabRow(selectedTabIndex = selectedTabIndex) {
                Tab(
                    text = { Text("Import") },
                    selected = true,
                    onClick = { selectedTabIndex = 0 }
                )
            }
        }
        val coroutineScope = rememberCoroutineScope()
        Box {
            ImportTab(selectedTabIndex == 0 && importingInstance == null, onGetInstance = {
                importingInstance = it
            })
            ConfirmImportTab(selectedTabIndex == 0 && importingInstance != null, importingInstance)
        }
    }
}

@Composable
fun ImportTab(visible: Boolean, onGetInstance: (GameInstance.CloudInstanceWithHeaders) -> Unit = {}) {
    val state = LocalLaunchyState
    AnimatedTab(visible) {
        Column {
            ComfyTitle("Import from link")

            ComfyContent {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    var urlText by remember { mutableStateOf("") }
                    var urlValid by remember { mutableStateOf(true) }
                    fun urlValid() = urlText.startsWith("https://") || urlText.startsWith("http://")
                    var failMessage: String? by remember { mutableStateOf(null) }

                    OutlinedTextField(
                        value = urlText,
                        singleLine = true,
                        isError = !urlValid || failMessage != null,
                        leadingIcon = { Icon(Icons.Rounded.Link, contentDescription = "Link") },
                        onValueChange = {
                            urlText = it
                            failMessage = null
                        },
                        label = { Text("Link") },
                        supportingText = {
                            if (!urlValid) Text("Must be valid URL")
                            else if (failMessage != null) Text(failMessage!!)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    TextButton(onClick = {
                        urlValid = urlValid()
                        if (!urlValid) return@TextButton
                        val taskKey = "importCloudInstance"
                        val downloadPath = Dirs.createTempCloudInstanceFile()
                        downloadPath.deleteIfExists()
                        AppDispatchers.IO.launch {
                            val cloudInstance = state.runTask(taskKey, InProgressTask("Importing cloud instance")) {
                                Downloader.download(urlText, downloadPath).mapCatching {
                                    when (it) {
                                        is Downloader.DownloadResult.AlreadyExists -> {
                                            failMessage = "Instance already downloaded locally"
                                            return@launch
                                        }

                                        is Downloader.DownloadResult.Success -> {
                                            GameInstance.CloudInstanceWithHeaders(
                                                config = GameInstanceConfig.read(downloadPath)
                                                    .showDialogOnError("Failed to read cloud instance")
                                                    .getOrThrow(),
                                                url = urlText,
                                                headers = it.modifyHeaders
                                            )
                                        }
                                    }
                                }.getOrElse {
                                    failMessage = "URL is not a valid instance file"
                                    return@launch
                                }
                            }
                            onGetInstance(cloudInstance)
                        }
                    }) {
                        Text("Import", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
//            PopularInstances()
        }
    }

}

@Composable
fun ConfirmImportTab(visible: Boolean, cloudInstance: GameInstance.CloudInstanceWithHeaders?) {
    if (cloudInstance == null) return
    val state = LocalLaunchyState
    AnimatedTab(visible) {
        val scrollState = rememberScrollState()
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.verticalScroll(scrollState)
        ) {
            ComfyTitle("Confirm import")
            ComfyContent {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    var nameText by remember { mutableStateOf(cloudInstance.config.name) }
                    fun nameValid() = nameText.matches(validInstanceNameRegex)
                    fun instanceExists() = Dirs.modpackConfigDir(nameText).exists()
                    var nameValid by remember { mutableStateOf(nameValid()) }
                    var instanceExists by remember { mutableStateOf(instanceExists()) }
                    var minecraftDir: String? by remember { mutableStateOf(null) }

                    OutlinedTextField(
                        value = nameText,
                        singleLine = true,
                        isError = !nameValid || instanceExists,
                        leadingIcon = { Icon(Icons.Rounded.TextFields, contentDescription = "Name") },
                        supportingText = {
                            if (!nameValid) Text("Name must be alphanumeric")
                            else if (instanceExists) Text("An instance with this name already exists")
                        },
                        onValueChange = {
                            nameText = it
                            instanceExists = false
                        },
                        label = { Text("Instance name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    InstanceProperties(
                        minecraftDir ?: nameText,
                        onChangeMinecraftDir = { minecraftDir = it }
                    )

                    TextButton(
                        onClick = {
                            nameValid = nameValid()
                            instanceExists = instanceExists()
                            if (!nameValid || instanceExists) return@TextButton
                            val editedConfig = cloudInstance.config.copy(
                                name = nameText,
                                overrideMinecraftDir = minecraftDir.takeIf { it?.isNotEmpty() == true }
                            )
                            GameInstance.createCloudInstance(
                                state, cloudInstance.copy(config = editedConfig)
                            )
                            screen = Screen.Default
                        }
                    ) {
                        Text("Confirm", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            ComfyWidth {
                InstanceCard(
                    cloudInstance.config.copy(name = "Preview"),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun PopularInstances() {
    val state = LocalLaunchyState
    val coroutineScope = rememberCoroutineScope()
    val popularInstances = remember {
        listOf(
            ""
        )
    }
    ComfyTitle("Popular instances")
    ComfyContent {
        LazyRow {
            items(popularInstances) {
//                    InstanceCard(it, modifier = Modifier.padding(8.dp))
            }
        }
    }
}
