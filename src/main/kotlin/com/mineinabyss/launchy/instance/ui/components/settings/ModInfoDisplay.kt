package com.mineinabyss.launchy.instance.ui.components.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.core.ui.LocalGameInstanceState
import com.mineinabyss.launchy.core.ui.components.Tooltip
import com.mineinabyss.launchy.instance.data.Mod
import com.mineinabyss.launchy.instance.data.ModGroup
import com.mineinabyss.launchy.instance.data.ToggleMods.setModConfigEnabled
import com.mineinabyss.launchy.instance.data.ToggleMods.setModEnabled
import com.mineinabyss.launchy.util.DesktopHelpers

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ModInfoDisplay(group: ModGroup, mod: Mod) {
    val state = LocalGameInstanceState
    val modEnabled by derivedStateOf { mod in state.toggles.enabledMods }
    val configEnabled by derivedStateOf { mod in state.toggles.enabledConfigs }
    var configExpanded by remember { mutableStateOf(false) }
    val configTabState by animateFloatAsState(targetValue = if (configExpanded) 180f else 0f)

    val surfaceColor = when (mod) {
        in state.queued.failures -> MaterialTheme.colorScheme.error
        in state.queued.deletions -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f)
        in state.queued.newDownloads -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.25f)
        in state.queued.updates -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.1f)
        else -> MaterialTheme.colorScheme.surface
    }

    val infoIcon: ImageVector? = when (mod) {
        in state.queued.failures -> Icons.Rounded.Error
        in state.queued.deletions -> Icons.Rounded.Delete
        in state.queued.newDownloads -> Icons.Rounded.Download
        in state.queued.updates -> Icons.Rounded.Update
        else -> null
    }
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = surfaceColor,
        onClick = { if (!group.forceEnabled && !group.forceDisabled) state.toggles.setModEnabled(mod, !modEnabled) }
    ) {
        if (state.downloads.inProgressMods.containsKey(mod) || state.downloads.inProgressConfigs.containsKey(mod)) {
            val modProgress = state.downloads.inProgressMods[mod]
            val configProgress = state.downloads.inProgressConfigs[mod]
            val downloaded = (modProgress?.bytesDownloaded ?: 0L) + (configProgress?.bytesDownloaded ?: 0L)
            val total = (modProgress?.totalBytes ?: 0L) + (configProgress?.totalBytes ?: 0L)
            LinearProgressIndicator(
                progress = if (total == 0L) 0f else downloaded.toFloat() / total,
                color = MaterialTheme.colorScheme.primaryContainer
            )
        }
        Column() {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Checkbox(
                    enabled = !group.forceEnabled && !group.forceDisabled,
                    checked = modEnabled,
                    onCheckedChange = { state.toggles.setModEnabled(mod, !modEnabled) }
                )

                if (infoIcon != null) Icon(infoIcon, "Mod Information", modifier = Modifier.padding(end = 8.dp))

                Row(Modifier.weight(6f)) {
                    Text(mod.info.name, style = MaterialTheme.typography.bodyLarge)
                    // build list of mods that are incompatible with this mod
                    val incompatibleMods = state.modpack.mods.mods
                        .filter { !mod.compatibleWith(it) }
                        .map { it.info.name }
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
                            Tooltip {
                                Text(
                                    text = "Open homepage",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable {
                            if (!mod.info.forceConfigDownload) state.toggles.setModConfigEnabled(
                                mod,
                                !configEnabled
                            )
                        }
                        .fillMaxWidth()
                ) {
                    Spacer(Modifier.width(20.dp))
                    Checkbox(
                        checked = configEnabled || mod.info.forceConfigDownload,
                        onCheckedChange = {
                            if (!mod.info.forceConfigDownload) state.toggles.setModConfigEnabled(mod, !configEnabled)
                        },
                        enabled = !mod.info.forceConfigDownload,
                    )
                    Column {
                        Text(
                            "Download our recommended configuration",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (mod.info.configDesc.isNotEmpty()) {
                            Spacer(Modifier.width(4.dp))
                            Text(
                                mod.info.configDesc,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.alpha(0.5f)
                            )
                        }
                    }
                }
            }
        }
    }
}
