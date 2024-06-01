package com.mineinabyss.launchy.instance.ui.components.settings

import androidx.compose.animation.*
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.HistoryEdu
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.core.ui.Constants
import com.mineinabyss.launchy.core.ui.Constants.SETTINGS_HORIZONTAL_PADDING
import com.mineinabyss.launchy.core.ui.LocalGameInstanceState
import com.mineinabyss.launchy.core.ui.components.Tooltip
import com.mineinabyss.launchy.instance.ui.components.buttons.InstallButton
import com.mineinabyss.launchy.instance.ui.components.buttons.RetryFailedButton

object InfoBarProperties {
    val height = 64.dp
}
@Composable
fun InfoBar(modifier: Modifier = Modifier) {
    val state = LocalLaunchyState
    val packState = LocalGameInstanceState
    Surface(
        tonalElevation = 2.dp,
        shadowElevation = 0.dp,
        shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
        modifier = Modifier.fillMaxWidth().height(InfoBarProperties.height).then(modifier),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = SETTINGS_HORIZONTAL_PADDING, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            InstallButton(
                state.processFor(packState.instance) == null
                        && !packState.downloads.isDownloading
                        && (packState.queued.areOperationsQueued || packState.queued.userAgreedModLoaders == null)
                        && state.inProgressTasks.isEmpty(),
                Modifier.width(Constants.SETTINGS_PRIMARY_BUTTON_WIDTH)
            )
            val failures = packState.queued.failures.isNotEmpty()
            AnimatedVisibility(failures) {
                RetryFailedButton(failures)
            }
            ActionButton(
                shown = packState.queued.areModLoaderUpdatesAvailable,
                icon = Icons.Rounded.HistoryEdu,
                desc = "Mod loader updates:\n${packState.queued.userAgreedModLoaders?.fullVersionName ?: "Not installed"} -> ${packState.modpack.modLoaders.fullVersionName}",
                count = 1
            )
            ActionButton(
                shown = packState.queued.areUpdatesQueued,
                icon = Icons.Rounded.Update,
                desc = "Queued updates",
                count = packState.queued.updates.size
            )
            ActionButton(
                shown = packState.queued.areNewDownloadsQueued,
                icon = Icons.Rounded.Download,
                desc = "Queued downloads for new mods",
                count = packState.queued.newDownloads.size
            )
            ActionButton(
                shown = packState.queued.areDeletionsQueued,
                icon = Icons.Rounded.Delete,
                desc = "Queued mod deletions",
                count = packState.queued.deletions.size
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
        }
    }
}
