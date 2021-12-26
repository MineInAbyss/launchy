package com.mineinabyss.launchy.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.LocalConfig
import com.mineinabyss.launchy.data.Mod
import com.mineinabyss.launchy.util.Option

@Composable
fun ModGroup(group: String, mods: Collection<Mod>) {
    var expanded by remember { mutableStateOf(false) }
    val config = LocalConfig

    Card(Modifier.padding(2.dp).fillMaxWidth().clickable { expanded = !expanded }) {
        Column {
            var groupChecked by remember { mutableStateOf(config.groups[group] ?: Option.DEFAULT) }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.height(40.dp)
            ) {
                TripleSwitch(groupChecked, onSwitch = {
                    config.groups[group] = it
                    groupChecked = it
                })
                Spacer(Modifier.width(10.dp))
                Text(
                    group, Modifier.weight(1f),
                    style = MaterialTheme.typography.h5,
                )
                Icon(Icons.Rounded.ArrowDropDown, "Show mods")
            }
            AnimatedVisibility(expanded) {
                Column {
                    for (mod in mods) ModInfo(mod, groupChecked)
                }
            }
        }
    }
}
