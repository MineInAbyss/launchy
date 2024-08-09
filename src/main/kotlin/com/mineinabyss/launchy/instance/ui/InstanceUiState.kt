package com.mineinabyss.launchy.instance.ui

import androidx.compose.ui.graphics.painter.BitmapPainter
import com.mineinabyss.launchy.util.InstanceKey

data class InstanceUiState(
    val title: String,
    val description: String,
    val isCloudInstance: Boolean,
    val logo: BitmapPainter?,
    val background: BitmapPainter?,
    val runningProcess: Process?,
    val hue: Float,
    val enabled: Boolean,
    val updatesAvailable: Boolean,
    val key: InstanceKey,
    val installedModLoader: String?
)
