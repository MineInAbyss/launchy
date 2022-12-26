package com.mineinabyss.launchy.ui.screens.settings

import androidx.compose.runtime.Composable
import com.mineinabyss.launchy.ui.TabIconBar
import com.mineinabyss.launchy.ui.screens.Screen
import com.mineinabyss.launchy.ui.screens.screen
import com.mineinabyss.launchy.ui.state.TopBar

@Composable
fun Tabs() {

    TabIconBar(
        TopBar,
        screen.transparentTopBar,
        tabIconScreens = setOf(Screen.Account),
        onTabIconClicked = { screen = Screen.Default }
    )
}
