package com.mineinabyss.launchy.instance.ui.components.settings.infobar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.HistoryEdu
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mineinabyss.launchy.core.ui.Constants
import com.mineinabyss.launchy.core.ui.Constants.SETTINGS_HORIZONTAL_PADDING
import com.mineinabyss.launchy.instance.ui.InstallState
import com.mineinabyss.launchy.instance.ui.InstanceViewModel
import com.mineinabyss.launchy.instance.ui.components.buttons.InstallButton
import com.mineinabyss.launchy.instance.ui.components.buttons.RetryFailedButton

@Composable
fun InfoBar(
    viewModel: InstanceViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val queuedState by viewModel.installState.collectAsState()
    val queue = when (queuedState) {
        is InstallState.Queued -> queuedState as InstallState.Queued
        else -> return
    }

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
            val installState by viewModel.installState.collectAsState()
            InstallButton(
                installState,
                Modifier.width(Constants.SETTINGS_PRIMARY_BUTTON_WIDTH),
                onClick = { viewModel.installMods() }
            )
            AnimatedVisibility(queue.failures.isNotEmpty()) {
                RetryFailedButton(
                    queue.failures.count(),
                    onClick = { viewModel.installMods(ignoreCachedCheck = true) }
                )
            }
            val queued = installState as? InstallState.Queued
            val instance by viewModel.instanceUiState.collectAsState()
            ActionButton(
                shown = queue.modLoaderChange != null,
                icon = Icons.Rounded.HistoryEdu,
                desc = "Mod loader updates:\n${instance?.installedModLoader ?: "Not installed"} -> ${queue.modLoaderChange}",
                count = 1
            )
            ActionButton(
                shown = queue.update.isNotEmpty(),
                icon = Icons.Rounded.Update,
                desc = "Queued updates",
                count = queue.update.size
            )
            ActionButton(
                shown = queue.install.isNotEmpty(),
                icon = Icons.Rounded.Download,
                desc = "Queued downloads for new mods",
                count = queue.install.size
            )
            ActionButton(
                shown = queue.remove.isNotEmpty(),
                icon = Icons.Rounded.Delete,
                desc = "Queued mod deletions",
                count = queue.remove.size
            )
        }
    }
}

