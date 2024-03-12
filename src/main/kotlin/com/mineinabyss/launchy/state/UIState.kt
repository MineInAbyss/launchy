package com.mineinabyss.launchy.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.mineinabyss.launchy.data.config.Config

class UIState(config: Config) {
    var preferHue: Float by mutableStateOf(config.preferHue ?: 0f)
    var fullscreen: Boolean by mutableStateOf(config.startInFullscreen)
}
