package com.mineinabyss.launchy.instance_creation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import com.mineinabyss.launchy.core.ui.components.ComfyWidth
import com.mineinabyss.launchy.instance_list.data.LocalInstancesDataSource

val validInstanceNameRegex = Regex("^[a-zA-Z0-9_ ]+$")

@Composable
fun NewInstance() {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var importingInstance: LocalInstancesDataSource.CloudInstanceWithHeaders? by remember { mutableStateOf(null) }
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
        Box {
            ImportTab(selectedTabIndex == 0 && importingInstance == null, onGetInstance = {
                importingInstance = it
            })
            ConfirmImportTab(selectedTabIndex == 0 && importingInstance != null, importingInstance)
        }
    }
}

