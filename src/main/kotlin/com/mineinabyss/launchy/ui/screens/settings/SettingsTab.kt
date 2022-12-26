package com.mineinabyss.launchy.ui.screens.settings

import androidx.compose.runtime.Composable
import com.mineinabyss.launchy.ui.TabIconBar
import com.mineinabyss.launchy.ui.screens.Screen
import com.mineinabyss.launchy.ui.screens.TransitionSlideUp
import com.mineinabyss.launchy.ui.screens.TranslucentTopBar
import com.mineinabyss.launchy.ui.screens.screen
import com.mineinabyss.launchy.ui.state.TopBar

@Composable
fun Tabs() {

    TranslucentTopBar(screen) {
        TransitionSlideUp(screen == Screen.Account) {
            AccountScreen()
        }
    }

    TabIconBar(
        TopBar,
        screen.transparentTopBar,
        tabIconScreens = setOf(Screen.Account),
        onTabIconClicked = { screen = Screen.Default }
    )
}
