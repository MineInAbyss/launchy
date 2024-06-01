package com.mineinabyss.launchy.instance.ui.components.buttons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.mineinabyss.launchy.core.ui.Screen
import com.mineinabyss.launchy.core.ui.screen

@Composable
fun SettingsButton() {
    Button(onClick = { screen = Screen.InstanceSettings }) {
        Icon(Icons.Rounded.Settings, contentDescription = "Settings")
        Text("Settings")
    }
}
