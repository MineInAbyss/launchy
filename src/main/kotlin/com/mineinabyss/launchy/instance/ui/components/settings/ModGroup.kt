package com.mineinabyss.launchy.instance.ui.components.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Difference
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.core.ui.LocalGameInstanceState
import com.mineinabyss.launchy.core.ui.components.Tooltip
import com.mineinabyss.launchy.instance.ui.ModGroupUiState
import com.mineinabyss.launchy.instance.ui.ModListInteractions

@Composable
fun ModGroup(
    group: ModGroupUiState,
    interactions: ModListInteractions
) {
    var expanded by remember { mutableStateOf(false) }
    val arrowRotationState by animateFloatAsState(targetValue = if (expanded) 180f else 0f)
    val state = LocalGameInstanceState

//    val modsChanged = mods.any {
//        it in state.queued.deletions || it in state.queued.newDownloads || it in state.queued.failures
//    }

    val tonalElevation by animateDpAsState(if (expanded) 1.6.dp else 1.dp)
    Column {
        Surface(
            tonalElevation = tonalElevation,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth(),
            onClick = { expanded = !expanded },
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.height(40.dp)
            ) {

                ToggleButtons(
                    onSwitch = { option -> interactions.onToggleGroup(option) },
                    group = group,
                    mods = group.mods
                )

                Spacer(Modifier.width(10.dp))
                Text(
                    group.title, Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge,
                )
                AnimatedVisibility(modsChanged, enter = fadeIn(), exit = fadeOut()) {
                    TooltipArea(
                        tooltip = { Tooltip("Some mods in this group have pending changes") }
                    ) {
                        Icon(Icons.Rounded.Difference, "Changes pending")
                    }
                }
                Spacer(Modifier.width(10.dp))
                Icon(Icons.Rounded.ArrowDropDown, "Show mods", Modifier.rotate(arrowRotationState))
                Spacer(Modifier.width(10.dp))
            }
        }
        AnimatedVisibility(expanded) {
            Surface(
                tonalElevation = 0.2.dp,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 8.dp, start = 10.dp)
            ) {
                Column {
                    for (mod in group.mods) key(mod.id) {
                        ModInfoDisplay(group, mod)
                    }
                }
            }
        }
    }
}
