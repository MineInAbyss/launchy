package com.mineinabyss.launchy.instance.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.vector.ImageVector
import com.mineinabyss.launchy.util.ModID

data class ModUiState(
    val id: ModID,
    val enabled: Boolean,
    val queueState: ModQueueState
)


enum class ModQueueState {
    RETRY_DOWNLOAD,
    DELETE,
    INSTALL,
    UPDATE,
    NONE;

    companion object {
        fun surfaceColor(state: ModQueueState) = when (state) {
            RETRY_DOWNLOAD -> MaterialTheme.colorScheme.error
            DELETE -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f)
            INSTALL -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.25f)
            UPDATE -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.1f)
            NONE -> MaterialTheme.colorScheme.surface
        }

        fun infoIcon(state: ModQueueState): ImageVector? = when (state) {
            RETRY_DOWNLOAD -> Icons.Rounded.Error
            DELETE -> Icons.Rounded.Delete
            INSTALL -> Icons.Rounded.Download
            UPDATE -> Icons.Rounded.Update
            NONE -> null
        }
    }
}
