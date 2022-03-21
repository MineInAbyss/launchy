package com.mineinabyss.launchy.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.Update
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.data.Group
import com.mineinabyss.launchy.data.Mod
import edu.stanford.ejalbert.BrowserLauncher
import java.awt.Desktop
import java.net.URI

object Browser {
    val desktop = Desktop.getDesktop()
    fun browse(url: String ) = synchronized(desktop) { desktop.browse(URI.create(url))}
}

@Composable
fun ModInfo(group: Group, mod: Mod) {
    val state = LocalLaunchyState
    val modEnabled by derivedStateOf { mod in state.enabledMods }

    var linkExpanded by remember { mutableStateOf(false) }
    val linkRotationState by animateFloatAsState(targetValue = if (linkExpanded) 180f else 0f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { if (!group.forceEnabled && !group.forceDisabled) state.setModEnabled(mod, !modEnabled) }
    ) {
        Column(Modifier.padding(2.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Switch(
                    enabled = !group.forceEnabled && !group.forceDisabled,
                    checked = modEnabled,
                    onCheckedChange = { state.setModEnabled(mod, !modEnabled) }
                )

                Row(Modifier.weight(6f)) {
                    Text(mod.name, style = MaterialTheme.typography.h6)
                }
                Text(
                    mod.desc,
                    style = MaterialTheme.typography.subtitle1,
                    modifier = Modifier.alpha(ContentAlpha.medium)
                )
                AnimatedVisibility(mod in state.queuedDeletions) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "Remove queued",
                        modifier = Modifier.alpha(ContentAlpha.medium),
                    )
                }
                AnimatedVisibility(mod !in state.upToDate) {
                    Icon(
                        imageVector = Icons.Rounded.Update,
                        contentDescription = "Update available",
                        modifier = Modifier.alpha(ContentAlpha.medium),
                    )
                }
                val coroutineScope = rememberCoroutineScope()
                if (mod.homepage != null)
                    IconButton(
                        modifier = Modifier
                            .alpha(ContentAlpha.medium)
                            .rotate(linkRotationState),
                        onClick = { BrowserLauncher().openURLinBrowser(mod.homepage) }) {
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
