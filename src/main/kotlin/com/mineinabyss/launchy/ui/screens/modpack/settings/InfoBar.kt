package com.mineinabyss.launchy.ui.screens.modpack.settings

import androidx.compose.animation.*
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Update
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
import com.mineinabyss.launchy.ui.screens.LocalModpackState
import com.mineinabyss.launchy.ui.screens.modpack.main.buttons.InstallButton

@Composable
fun InfoBar() {
    val state = LocalLaunchyState
    val packState = LocalModpackState
    Surface(
        tonalElevation = 2.dp,
        shadowElevation = 0.dp,
        shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        if (packState.downloads.isDownloading) {
            val inProgress = (packState.downloads.inProgressMods + packState.downloads.inProgressConfigs).values
            val totalBytesToDownload = inProgress.sumOf { it.totalBytes }
            val totalBytesDownloaded = inProgress.sumOf { it.bytesDownloaded }.toFloat()

            LinearProgressIndicator(
                progress = if (totalBytesToDownload == 0L) 0f else totalBytesDownloaded / totalBytesToDownload,
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
                state.processFor(packState.modpack.info) == null
                        && !packState.downloads.isDownloading
                        && (packState.queued.areOperationsQueued || packState.userAgreedDeps == null),
                Modifier.width(Constants.SETTINGS_PRIMARY_BUTTON_WIDTH)
            )
            Spacer(Modifier.width(12.dp))
            ActionButton(
                shown = packState.queued.areUpdatesQueued,
                icon = Icons.Rounded.Update,
                desc = "Queued updates",
                count = packState.queued.updates.size
            )
            ActionButton(
                shown = packState.queued.areInstallsQueued,
                icon = Icons.Rounded.Download,
                desc = "Queued downloads for new mods",
                count = packState.queued.installs.size
            )
            ActionButton(
                shown = packState.queued.areDeletionsQueued,
                icon = Icons.Rounded.Delete,
                desc = "Queued mod deletions",
                count = packState.queued.deletions.size
            )

            if (packState.downloads.failed.isNotEmpty()) Text(
                text = "Failed downloads: ${packState.downloads.failed.size}",
                style = MaterialTheme.typography.bodySmall,
            )
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
