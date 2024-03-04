package com.mineinabyss.launchy.ui.screens

sealed class Screen(
    val transparentTopBar: Boolean = false,
    val showTitle: Boolean = false,
    val showSidebar: Boolean = false,
) {
    object Default : Screen(transparentTopBar = true, showTitle = true, showSidebar = true)
    object NewInstance: Screen(transparentTopBar = true, showTitle = true, showSidebar = true)

    object Settings : Screen(showTitle = true)
    object Modpack : Screen(transparentTopBar = true)
}
