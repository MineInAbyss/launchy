package com.mineinabyss.launchy.instance.ui.components.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.layout.*
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.core.ui.components.Tooltip
import com.mineinabyss.launchy.instance.ui.ModGroupUiState
import com.mineinabyss.launchy.instance.ui.ModInteractions
import com.mineinabyss.launchy.instance.ui.ModQueueState
import com.mineinabyss.launchy.instance.ui.ModUiState
import com.mineinabyss.launchy.util.DesktopHelpers
import com.mineinabyss.launchy.util.Option

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ModInfoDisplay(
    group: ModGroupUiState,
    mod: ModUiState,
    interactions: ModInteractions,
) {
    val surfaceColor = ModQueueState.surfaceColor(mod.queueState)
    val infoIcon = ModQueueState.infoIcon(mod.queueState)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = surfaceColor,
        onClick = { if (group.force == Option.DEFAULT) interactions.onToggleMod(!mod.enabled) }
    ) {
        if (mod.installProgress != null) {
            val downloaded = mod.installProgress.bytesDownloaded
            val total = mod.installProgress.totalBytes
            LinearProgressIndicator(
                progress = if (total == 0L) 0f else downloaded.toFloat() / total,
                color = MaterialTheme.colorScheme.primaryContainer
            )
        }
        var configExpanded by remember { mutableStateOf(false) }
        val configTabState by animateFloatAsState(targetValue = if (configExpanded) 180f else 0f)

        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Checkbox(
                    enabled = group.force == Option.DEFAULT,
                    checked = mod.enabled,
                    onCheckedChange = { interactions.onToggleMod(!mod.enabled) }
                )

                if (infoIcon != null) Icon(infoIcon, "Mod Information", modifier = Modifier.padding(end = 8.dp))

                Row(Modifier.weight(6f)) {
                    Text(mod.info.name, style = MaterialTheme.typography.bodyLarge)
                    // build list of mods that are incompatible with this mod
                    val incompatibleMods = mod.incompatibleWith
                    if (mod.info.requires.isNotEmpty() || incompatibleMods.isNotEmpty()) {
                        TooltipArea(
                            modifier = Modifier.alpha(0.5f),
                            tooltip = {
                                Tooltip {
                                    if (mod.info.requires.isNotEmpty()) {
                                        Text(
                                            text = "Requires: ${mod.info.requires.joinToString()}",
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    }
                                    if (incompatibleMods.isNotEmpty()) {
                                        Text(
                                            text = "Incompatible with: ${incompatibleMods.joinToString()}",
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Info,
                                contentDescription = "Requires",
                                modifier = Modifier.scale(0.75f)
                            )
                        }
                    }
                }
                Text(
                    mod.info.desc,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.alpha(0.5f)
                )

                if (mod.info.configUrl.isNotEmpty()) {
                    TooltipArea(
                        modifier = Modifier.alpha(0.5f),
                        tooltip = { Tooltip("Config") }
                    ) {
                        IconButton(onClick = { configExpanded = !configExpanded }) {
                            Icon(
                                imageVector = Icons.Rounded.Settings,
                                contentDescription = "ConfigTab",
                                modifier = Modifier.rotate(configTabState)
                            )
                        }
                    }
                }
                if (mod.info.homepage.isNotEmpty()) {
                    TooltipArea(
                        modifier = Modifier.alpha(0.5f),
                        tooltip = {
                            Tooltip("Open homepage")
                        }
                    ) {
                        IconButton(onClick = { DesktopHelpers.browse(mod.info.homepage) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                                contentDescription = "Homepage"
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(configExpanded) {
                ModConfigOptions()
            }
        }
    }
}


