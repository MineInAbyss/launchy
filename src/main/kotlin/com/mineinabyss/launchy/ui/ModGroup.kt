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
import com.mineinabyss.launchy.LaunchyState
import com.mineinabyss.launchy.data.GroupName
import com.mineinabyss.launchy.data.Mod
import com.mineinabyss.launchy.util.Option

@Composable
fun ModGroup(groupName: GroupName, mods: Collection<Mod>) {
    var expanded by remember { mutableStateOf(false) }
    val state = LaunchyState

    Card(Modifier.padding(2.dp).fillMaxWidth().clickable { expanded = !expanded }) {
        Column {
            val groupOption = state.groups.getOrDefault(groupName, Option.DEFAULT)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.height(40.dp)
            ) {
                TripleSwitch(groupOption, onSwitch = {
                    state.groups[groupName] = it
                })
                Spacer(Modifier.width(10.dp))
                Text(
                    groupName, Modifier.weight(1f),
                    style = MaterialTheme.typography.h5,
                )
                Icon(Icons.Rounded.ArrowDropDown, "Show mods")
            }
            AnimatedVisibility(expanded) {
                Column {
                    for (mod in mods) ModInfo(mod, groupOption)
                }
            }
        }
    }
}
