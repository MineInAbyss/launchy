package com.mineinabyss.launchy.core.ui

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen(
    val transparentTopBar: Boolean = true,
    val showTitle: Boolean = false,
    val showSidebar: Boolean = false,
) {
    interface OnLeftSidebar

    object Default : Screen(transparentTopBar = true, showTitle = true, showSidebar = true), OnLeftSidebar
    object NewInstance: Screen(transparentTopBar = true, showTitle = true, showSidebar = true), OnLeftSidebar
    object Settings : Screen(transparentTopBar = true, showTitle = true, showSidebar = true), OnLeftSidebar

    object InstanceSettings : Screen(showTitle = true)
    object Instance : Screen(transparentTopBar = true)

}
