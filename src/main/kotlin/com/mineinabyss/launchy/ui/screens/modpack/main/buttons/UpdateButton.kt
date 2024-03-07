package com.mineinabyss.launchy.ui.screens.modpack.main.buttons

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.logic.Instances.updateInstance
import com.mineinabyss.launchy.ui.screens.LocalModpackState

@Composable
fun UpdateButton() {
    val state = LocalLaunchyState
    val pack = LocalModpackState
    Box {
        Button(onClick = {
            pack.instance.updateInstance(state)
        }) {
            Icon(Icons.Rounded.Update, contentDescription = "Update")
            Text("Update Available")
        }
    }
}
