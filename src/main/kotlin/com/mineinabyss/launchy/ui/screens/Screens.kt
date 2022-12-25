package com.mineinabyss.launchy.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.ui.AppTopBar
import com.mineinabyss.launchy.ui.screens.main.MainScreen
import com.mineinabyss.launchy.ui.screens.settings.AccountScreen
import com.mineinabyss.launchy.ui.screens.settings.ModsScreen
import com.mineinabyss.launchy.ui.state.TopBar

sealed class Screen(val transparentTopBar: Boolean = false) {
    object Default : Screen(transparentTopBar = true)
    object Mods : Screen()
    object Account : Screen()
}

var screen: Screen by mutableStateOf(Screen.Default)

@Composable
fun Screens() {

    TransitionFade(screen == Screen.Default) {
        MainScreen()
    }

    TranslucentTopBar(screen) {
        TransitionSlideUp(screen == Screen.Mods) {
            ModsScreen()
        }
    }

    TranslucentTopBar(screen) {
        TransitionSlideUp(screen == Screen.Account) {
            AccountScreen()
        }
    }

    AppTopBar(
        TopBar,
        screen.transparentTopBar,
        showBackButton = screen != Screen.Default,
        onBackButtonClicked = { screen = Screen.Default }
    )
}

@Composable
fun TranslucentTopBar(currentScreen: Screen, content: @Composable () -> Unit) {
    Column {
        AnimatedVisibility(!currentScreen.transparentTopBar, enter = fadeIn(), exit = fadeOut()) {
            Spacer(Modifier.height(40.dp))
        }
        content()
    }
}

@Composable
fun TransitionFade(enabled: Boolean, content: @Composable () -> Unit) {
    AnimatedVisibility(enabled, enter = fadeIn(), exit = fadeOut()) {
        content()
    }
}

@Composable
fun TransitionSlideUp(enabled: Boolean, content: @Composable () -> Unit) {
    AnimatedVisibility(
        enabled,
        enter = fadeIn() + slideIn(initialOffset = { IntOffset(0, 100) }),
        exit = fadeOut() + slideOut(targetOffset = { IntOffset(0, 100) }),
    ) {
        content()
    }
}

