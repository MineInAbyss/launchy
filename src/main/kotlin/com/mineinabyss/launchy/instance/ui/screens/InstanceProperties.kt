package com.mineinabyss.launchy.instance.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FileOpen
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.core.ui.components.DirectoryDialog

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
