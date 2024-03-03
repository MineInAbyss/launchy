package com.mineinabyss.launchy.ui.screens

import com.mineinabyss.launchy.state.modpack.ModpackState

sealed class Screen(val transparentTopBar: Boolean = false) {
    object Default : Screen()
    object Settings : Screen()
    object Modpack : Screen(transparentTopBar = true)
}
