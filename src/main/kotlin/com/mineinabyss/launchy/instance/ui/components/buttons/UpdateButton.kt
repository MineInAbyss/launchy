package com.mineinabyss.launchy.instance.ui.components.buttons

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun UpdateButton(onClick: () -> Unit = {}) {
    Box {
        Button(onClick) {
            Icon(Icons.Rounded.Update, contentDescription = "Update")
            Text("Update Available")
        }
    }
}
