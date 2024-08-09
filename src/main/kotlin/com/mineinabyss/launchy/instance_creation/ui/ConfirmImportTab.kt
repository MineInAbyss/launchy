package com.mineinabyss.launchy.instance_creation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.core.ui.components.AnimatedTab
import com.mineinabyss.launchy.core.ui.components.ComfyContent
import com.mineinabyss.launchy.core.ui.components.ComfyTitle
import com.mineinabyss.launchy.core.ui.components.ComfyWidth
import com.mineinabyss.launchy.core.ui.screens.Screen
import com.mineinabyss.launchy.core.ui.screens.screen
import com.mineinabyss.launchy.instance.ui.screens.InstanceProperties
import com.mineinabyss.launchy.instance_list.data.LocalInstancesDataSource
import com.mineinabyss.launchy.instance_list.ui.components.InstanceCard
import com.mineinabyss.launchy.util.Dirs
import kotlin.io.path.exists

@Composable
fun ConfirmImportTab(
    visible: Boolean,
    cloudInstance: LocalInstancesDataSource.CloudInstanceWithHeaders?
) {
    if (cloudInstance == null) return
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

                    TextField(
                        value = nameText,
                        onValueChange = {
                            nameText = it
                            instanceExists = false
                        },
                        singleLine = true,
                        isError = !nameValid || instanceExists,
                        leadingIcon = { Icon(Icons.Rounded.TextFields, contentDescription = "Name") },
                        supportingText = {
                            if (!nameValid) Text("Name must be alphanumeric")
                            else if (instanceExists) Text("An instance with this name already exists")
                        },
                        label = { Text("Instance name") },
                        modifier = Modifier.fillMaxWidth(),
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
                            GameInstanceDataSource.createCloudInstance(
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
