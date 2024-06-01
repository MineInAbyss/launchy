package com.mineinabyss.launchy.instance.ui.screens

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.mineinabyss.launchy.core.ui.components.AnimatedTab
import com.mineinabyss.launchy.core.ui.components.ComfyWidth

@Composable
@Preview
fun InstanceSettingsScreen() {
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
                    ModManagementTab()
                }
                AnimatedTab(selectedTabIndex == 1) {
                    OptionsTab()
                }
            }
        }
    }
}

