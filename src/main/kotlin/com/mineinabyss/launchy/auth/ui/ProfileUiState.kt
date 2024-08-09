package com.mineinabyss.launchy.auth.ui

import androidx.compose.ui.graphics.painter.BitmapPainter

data class ProfileUiState(
    val username: String,
    val avatar: BitmapPainter?,
)
