package com.mineinabyss.launchy.data

import androidx.compose.ui.unit.dp

object Constants {
    val SETTINGS_HORIZONTAL_PADDING = 10.dp
    val SETTINGS_PRIMARY_BUTTON_WIDTH = 140.dp

    val APP_VERSION = System.getProperty("jpackage.app-version") ?: null
    val GITHUB_REPO = "MineInAbyss/launchy"
}
