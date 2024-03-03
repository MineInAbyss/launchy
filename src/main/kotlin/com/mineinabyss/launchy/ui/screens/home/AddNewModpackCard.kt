package com.mineinabyss.launchy.ui.screens.home

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
import com.mineinabyss.launchy.ui.screens.Dialog
import com.mineinabyss.launchy.ui.screens.dialog

@Composable
fun AddNewModpackCard(modifier: Modifier = Modifier) {
    val highlightColor = MaterialTheme.colorScheme.secondary
    Surface(
        border = BorderStroke(3.dp, highlightColor),
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.height(ModpackCardStyle.cardHeight).clickable { dialog = Dialog.AddModpack }
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
