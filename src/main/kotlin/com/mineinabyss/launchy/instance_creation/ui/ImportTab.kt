package com.mineinabyss.launchy.instance_creation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.core.ui.components.AnimatedTab
import com.mineinabyss.launchy.core.ui.components.ComfyContent
import com.mineinabyss.launchy.core.ui.components.ComfyTitle
import com.mineinabyss.launchy.downloads.data.Downloader
import com.mineinabyss.launchy.instance.data.storage.InstanceConfig
import com.mineinabyss.launchy.util.AppDispatchers
import com.mineinabyss.launchy.util.Dirs
import com.mineinabyss.launchy.util.InProgressTask
import kotlinx.coroutines.launch
import kotlin.io.path.deleteIfExists

@Composable
fun ImportTab(
    visible: Boolean,
    onGetInstance: (GameInstanceDataSource.CloudInstanceWithHeaders) -> Unit = {}
) {
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
                                            GameInstanceDataSource.CloudInstanceWithHeaders(
                                                config = InstanceConfig.read(downloadPath)
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
