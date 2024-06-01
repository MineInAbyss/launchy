package com.mineinabyss.launchy.instance.ui.components.buttons

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.core.ui.LocalGameInstanceState

@Composable
fun UpdateInfoButton() {
    val state = LocalLaunchyState
    val packState = LocalGameInstanceState
    var toggled by remember { mutableStateOf(false) }
    Button(onClick = { toggled = !toggled }, shape = RoundedCornerShape(20.dp)) {
        Column {
            val queued = packState.queued

            Row {
                Icon(Icons.Rounded.Update, contentDescription = "Updates")
                Text("${queued.newDownloads.size + queued.deletions.size} Updates")
            }

            AnimatedVisibility(
                toggled,
                enter = expandIn(tween(200)) + fadeIn(tween(200, 100)),
                exit = fadeOut() + shrinkOut(tween(200, 100))
            ) {
                Column {
                    InfoText(
                        shown = queued.areUpdatesQueued,
                        icon = Icons.Rounded.Update,
                        desc = "Update",
                        extra = queued.updates.size.toString()
                    )
                    InfoText(
                        shown = queued.areNewDownloadsQueued,
                        icon = Icons.Rounded.Download,
                        desc = "Download",
                        extra = queued.newDownloads.size.toString()
                    )
                    InfoText(
                        shown = queued.areDeletionsQueued,
                        icon = Icons.Rounded.Delete,
                        desc = "Remove",
                        extra = queued.deletions.size.toString()
                    )
                }
            }
        }
    }
}

@Composable
fun InfoText(shown: Boolean, icon: ImageVector, desc: String, extra: String = "") {
    if (shown) Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, desc)
        Text(desc, Modifier.padding(5.dp))
        Text(extra)
    }
}


