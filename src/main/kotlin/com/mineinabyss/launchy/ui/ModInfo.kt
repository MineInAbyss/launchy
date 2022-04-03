package com.mineinabyss.launchy.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.data.Group
import com.mineinabyss.launchy.data.Mod
import edu.stanford.ejalbert.BrowserLauncher
import java.awt.Desktop
import java.net.URI

object Browser {
    val desktop = Desktop.getDesktop()
    fun browse(url: String) = synchronized(desktop) { desktop.browse(URI.create(url)) }
}

@Composable
fun ModInfo(group: Group, mod: Mod) {
    val state = LocalLaunchyState
    val modEnabled by derivedStateOf { mod in state.enabledMods }
    val configEnabled by derivedStateOf { mod in state.enabledConfigs }
    var configExpanded by remember { mutableStateOf(false) }
    val configTabState by animateFloatAsState(targetValue = if (configExpanded) 180f else 0f)

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
                Checkbox(
                    enabled = !group.forceEnabled && !group.forceDisabled,
                    checked = modEnabled,
                    onCheckedChange = { state.setModEnabled(mod, !modEnabled) }
                )

                Row(Modifier.weight(6f)) {
                    Text(mod.name, style = MaterialTheme.typography.bodySmall)
                }
                Text(
                    mod.desc,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.alpha(0.5f)
                )
                AnimatedVisibility(mod in state.queuedDeletions) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "Remove queued",
                        modifier = Modifier.alpha(0.5f),
                    )
                }
                AnimatedVisibility(mod !in state.upToDate) {
                    Icon(
                        imageVector = Icons.Rounded.Update,
                        contentDescription = "Update available",
                        modifier = Modifier.alpha(0.5f),
                    )
                }
                val coroutineScope = rememberCoroutineScope()
                if (mod.homepage != null)
                    IconButton(
                        modifier = Modifier
                            .alpha(0.5f),
                        onClick = { BrowserLauncher().openURLinBrowser(mod.homepage) }) {
                        Icon(
                            imageVector = Icons.Rounded.Link,
                            contentDescription = "URL"
                        )
                    }
                if (mod.configUrl != null) {
                    IconButton(
                        modifier = Modifier.alpha(0.5f).rotate(configTabState),
                        onClick = { configExpanded = !configExpanded }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "ConfigTab"
                        )
                    }
                }
            }
            AnimatedVisibility(configExpanded) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Spacer(Modifier.width(20.dp))
                    Checkbox(
                        checked = configEnabled || mod.forceConfigDownload,
                        onCheckedChange = {
                            if (!mod.forceConfigDownload) state.setModConfigEnabled(mod, !configEnabled)
                        },
                        enabled = !mod.forceConfigDownload,
                    )
                    Text(
                        "Download our recommended configuration",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 16.sp
                    )

//                    IconButton(
//                        modifier = Modifier
//                            .alpha(ContentAlpha.medium)
//                            .rotate(linkRotationState),
//                        onClick = { BrowserLauncher().openURLinBrowser(mod.configUrl) }) {
//                        Icon(
//                            imageVector = Icons.Rounded.Info,
//                            contentDescription = "URL"
//                        )
//                    }
                }
            }
        }
    }
}
