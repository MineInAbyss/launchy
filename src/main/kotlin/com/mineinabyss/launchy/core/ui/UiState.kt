package com.mineinabyss.launchy.core.ui

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.mineinabyss.launchy.config.data.Config

val LocalUiState = compositionLocalOf<UiState> { error("No UiState provided") }

//TODO move into viewmodel
class UiState(config: Config) {
    var preferHue: Float by mutableStateOf(config.preferHue ?: 0f)
    var fullscreen: Boolean by mutableStateOf(config.startInFullscreen)
}
