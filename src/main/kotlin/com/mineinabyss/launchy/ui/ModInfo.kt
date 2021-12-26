package com.mineinabyss.launchy.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.HelpOutline
import androidx.compose.material.icons.rounded.Link
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.LocalConfig
import com.mineinabyss.launchy.data.Mod
import com.mineinabyss.launchy.util.Option

@Composable
fun ModInfo(mod: Mod, groupEnabled: Option) {
    val config = LocalConfig
    var modEnabled by remember { mutableStateOf(mod.name in config.enabledMods) }
    var showDesc by remember { mutableStateOf(false) }
    var linkExpanded by remember { mutableStateOf(false) }
    val qRotationState by animateFloatAsState(targetValue = if (showDesc) 180f else 0f)
    val linkRotationState by animateFloatAsState(targetValue = if (linkExpanded) 180f else 0f)
    remember(modEnabled) {
        if (modEnabled)
            config.enabledMods += mod.name
        else
            config.enabledMods -= mod.name
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { modEnabled = !modEnabled }
    ) {
        Column(Modifier.padding(2.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Switch(
                    enabled = groupEnabled == Option.DEFAULT,
                    checked = groupEnabled == Option.ENABLED || (modEnabled && groupEnabled != Option.DISABLED),
                    onCheckedChange = { modEnabled = !modEnabled }
                )

                Row(Modifier.weight(6f)) {
                    Text(mod.name, style = MaterialTheme.typography.h6)
                }
                AnimatedVisibility(showDesc) {
                    Text(
                        mod.desc,
                        style = MaterialTheme.typography.subtitle1,
                        modifier = Modifier.alpha(ContentAlpha.medium)
                    )
                }
                IconButton(
                    modifier = Modifier
                        .alpha(ContentAlpha.medium)
                        .rotate(qRotationState),
                    onClick = { showDesc = !showDesc }) {
                    Icon(
                        imageVector = Icons.Rounded.HelpOutline,
                        contentDescription = "Drop-Down Arrow"
                    )
                }
                IconButton(
                    modifier = Modifier
                        .alpha(ContentAlpha.medium)
                        .rotate(linkRotationState),
                    onClick = { linkExpanded = !linkExpanded }) {
                    Icon(
                        imageVector = Icons.Rounded.Link,
                        contentDescription = "URL"
                    )
                }
            }
            AnimatedVisibility(linkExpanded) {
                Text(
                    mod.url,
                    style = MaterialTheme.typography.subtitle2,
                    modifier = Modifier.alpha(ContentAlpha.medium)
                )
            }
        }
    }
}
