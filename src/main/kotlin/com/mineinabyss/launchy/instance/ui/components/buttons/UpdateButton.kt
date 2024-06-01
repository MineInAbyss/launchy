package com.mineinabyss.launchy.instance.ui.components.buttons

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.core.ui.LocalGameInstanceState
import com.mineinabyss.launchy.instance_list.data.InstanceRepository.updateInstance

@Composable
fun UpdateButton() {
    val state = LocalLaunchyState
    val pack = LocalGameInstanceState
    Box {
        Button(onClick = {
            pack.instance.updateInstance(state)
        }) {
            Icon(Icons.Rounded.Update, contentDescription = "Update")
            Text("Update Available")
        }
    }
}
