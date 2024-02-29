package com.mineinabyss.launchy.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.ui.AppTopBar
import com.mineinabyss.launchy.ui.auth.AuthDialog
import com.mineinabyss.launchy.ui.screens.main.MainScreen
import com.mineinabyss.launchy.ui.screens.settings.SettingsScreen
import com.mineinabyss.launchy.ui.state.TopBar
import com.mineinabyss.launchy.ui.state.windowScope

sealed class Screen(val transparentTopBar: Boolean = false) {
    object Default : Screen(transparentTopBar = true)
    object Settings : Screen()
}

sealed interface Dialog {
    object None : Dialog
    object Auth : Dialog
}

sealed interface Progress {
    object None : Progress
    object Animated : Progress
    class Percent(val percent: Float) : Progress
}

var screen: Screen by mutableStateOf(Screen.Default)

var dialog: Dialog by mutableStateOf(Dialog.None)

var progress: Progress by mutableStateOf(Progress.Animated)

@Composable
fun Screens() {
    TransitionFade(screen == Screen.Default) {
        MainScreen()
    }

    TranslucentTopBar(screen) {
        TransitionSlideUp(screen == Screen.Settings) {
            SettingsScreen()
        }
    }

    AppTopBar(
        TopBar,
        screen.transparentTopBar,
        showBackButton = screen != Screen.Default,
        onBackButtonClicked = { screen = Screen.Default }
    )


    when (dialog) {
        Dialog.None -> {}
        Dialog.Auth -> {
            AuthDialog(
                windowScope,
                onDismissRequest = { dialog = Dialog.None },
                onComplete = { dialog = Dialog.None },
            )
        }
    }
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

