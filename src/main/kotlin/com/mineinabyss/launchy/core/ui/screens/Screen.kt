package com.mineinabyss.launchy.core.ui.screens

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen(
    val transparentTopBar: Boolean = true,
    val showTitle: Boolean = false,
    val showSidebar: Boolean = false,
) {
    interface OnLeftSidebar

    data object Default : Screen(transparentTopBar = true, showTitle = true, showSidebar = true), OnLeftSidebar
    data object NewInstance : Screen(transparentTopBar = true, showTitle = true, showSidebar = true), OnLeftSidebar
    data object Settings : Screen(transparentTopBar = true, showTitle = true, showSidebar = true), OnLeftSidebar

    data object InstanceSettings : Screen(showTitle = true)
    data object Instance : Screen(transparentTopBar = true)

}
