package com.mineinabyss.launchy.instance.ui

import androidx.compose.ui.graphics.painter.BitmapPainter

data class InstanceUiState(
    val title: String,
    val description: String,
    val isCloudInstance: Boolean,
    val logo: BitmapPainter?,
    val background: BitmapPainter?,
    val runningProcess: Process?,
)
