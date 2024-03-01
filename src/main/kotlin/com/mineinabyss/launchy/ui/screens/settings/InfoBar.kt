package com.mineinabyss.launchy.ui.screens.settings

import androidx.compose.animation.*
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.data.Constants
import com.mineinabyss.launchy.data.Constants.SETTINGS_HORIZONTAL_PADDING
import com.mineinabyss.launchy.ui.elements.Tooltip
import com.mineinabyss.launchy.ui.screens.main.buttons.InstallButton

@Composable
fun InfoBar() {
    val state = LocalLaunchyState
    Surface(
        tonalElevation = 2.dp,
        shadowElevation = 0.dp,
        shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        if (state.isDownloading) {
            val totalBytesToDownload =
                state.downloading.values.sumOf { it.totalBytes } + state.downloadingConfigs.values.sumOf { it.totalBytes }
            val totalBytesDownloaded =
                state.downloading.values.sumOf { it.bytesDownloaded } + state.downloadingConfigs.values.sumOf { it.bytesDownloaded }
            LinearProgressIndicator(
                progress = if (totalBytesToDownload == 0L) 0f else totalBytesDownloaded.toFloat() / totalBytesToDownload,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primaryContainer
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = SETTINGS_HORIZONTAL_PADDING, vertical = 6.dp)
        ) {
            InstallButton(
                state.currentLaunchProcess == null && !state.isDownloading && state.operationsQueued && state.minecraftValid,
                Modifier.width(Constants.SETTINGS_PRIMARY_BUTTON_WIDTH)
            )
            Spacer(Modifier.width(12.dp))
            ActionButton(
                shown = !state.minecraftValid,
                icon = Icons.Rounded.Error,
                desc = "No minecraft installation found",
            )
            ActionButton(
                shown = state.updatesQueued,
                icon = Icons.Rounded.Update,
                desc = "Queued updates",
                count = state.queuedUpdates.size
            )
            ActionButton(
                shown = state.installsQueued,
                icon = Icons.Rounded.Download,
                desc = "Queued downloads for new mods",
                count = state.queuedInstalls.size
            )
            ActionButton(
                shown = state.deletionsQueued,
                icon = Icons.Rounded.Delete,
                desc = "Queued mod deletions",
                count = state.queuedDeletions.size
            )

            if (state.isDownloading) {
                // Show download progress
                val totalBytesToDownload =
                    state.downloading.values.sumOf { it.totalBytes } + state.downloadingConfigs.values.sumOf { it.totalBytes }
                val totalBytesDownloaded =
                    state.downloading.values.sumOf { it.bytesDownloaded } + state.downloadingConfigs.values.sumOf { it.bytesDownloaded }
                Text(
                    text = "Downloading ${state.downloading.size + state.downloadingConfigs.size} files (${totalBytesDownloaded / 1000} / ${totalBytesToDownload / 1000} KB)",
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            if (state.failedDownloads.isNotEmpty()) {
                Text(
                    text = "Failed downloads: ${state.failedDownloads.size}",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
fun ActionButton(shown: Boolean, icon: ImageVector, desc: String, count: Int? = null) {
    AnimatedVisibility(
        shown,
        enter = fadeIn() + expandHorizontally(expandFrom = Alignment.Start),
        exit = fadeOut() + shrinkHorizontally(shrinkTowards = Alignment.Start)
    ) {
        Row {
            TooltipArea(tooltip = { Tooltip(desc) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, desc, modifier = Modifier.padding(end = 4.dp).alignByBaseline())
                    if (count != null) {
                        val animatedCount by animateIntAsState(targetValue = count)
                        Text(animatedCount.toString(), modifier = Modifier.alignByBaseline())
                    }
                }
            }
            Spacer(Modifier.width(12.dp))
        }
    }
}
