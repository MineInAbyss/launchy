package com.mineinabyss.launchy.instance.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import com.mineinabyss.launchy.instance.data.storage.ModConfig
import com.mineinabyss.launchy.util.ModID
import com.mineinabyss.launchy.util.ModName
import com.mineinabyss.launchy.util.Progress

data class ModUiState(
    val id: ModID,
    val enabled: Boolean,
    val configEnabled: Boolean,
    val queueState: ModQueueState,
    val info: ModConfig,
    val incompatibleWith: List<ModName>,
    val dependsOn: List<ModName>,
    val installProgress: Progress?,
)


enum class ModQueueState {
    RETRY_DOWNLOAD,
    DELETE,
    INSTALL,
    UPDATE,
    NONE;

    companion object {
        @Composable
        fun surfaceColor(state: ModQueueState) = remember(state) {
            when (state) {
                RETRY_DOWNLOAD -> MaterialTheme.colorScheme.error
                DELETE -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f)
                INSTALL -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.25f)
                UPDATE -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.1f)
                NONE -> MaterialTheme.colorScheme.surface
            }
        }

        @Composable
        fun infoIcon(state: ModQueueState): ImageVector? = remember(state) {
            when (state) {
                RETRY_DOWNLOAD -> Icons.Rounded.Error
                DELETE -> Icons.Rounded.Delete
                INSTALL -> Icons.Rounded.Download
                UPDATE -> Icons.Rounded.Update
                NONE -> null
            }
        }
    }
}
