package com.mineinabyss.launchy.instance_list.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.core.ui.screens.Screen
import com.mineinabyss.launchy.core.ui.screens.screen

@Composable
fun AddNewModpackCard(modifier: Modifier = Modifier) {
    val highlightColor = MaterialTheme.colorScheme.secondary
    Surface(
        border = BorderStroke(3.dp, highlightColor),
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.height(InstanceCardStyle.cardHeight).clickable { screen = Screen.NewInstance }
    ) {
        Box {
            Row(Modifier.align(Alignment.Center), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Rounded.Add,
                    "Add modpack",
                    Modifier.size(40.dp),
                    tint = highlightColor
                )
            }
        }
    }
}
