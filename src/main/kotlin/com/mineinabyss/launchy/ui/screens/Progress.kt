package com.mineinabyss.launchy.ui.screens

sealed interface Progress {
    object None : Progress
    object Animated : Progress
    class Percent(val percent: Float) : Progress
}
