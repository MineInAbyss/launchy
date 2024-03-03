package com.mineinabyss.launchy.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.state.modpack.ModpackState
import com.mineinabyss.launchy.ui.AppTopBar
import com.mineinabyss.launchy.ui.auth.AuthDialog
import com.mineinabyss.launchy.ui.elements.LaunchyDialog
import com.mineinabyss.launchy.ui.screens.home.HomeScreen
import com.mineinabyss.launchy.ui.screens.modpack.main.ModpackScreen
import com.mineinabyss.launchy.ui.screens.modpack.settings.SettingsScreen
import com.mineinabyss.launchy.ui.state.TopBar
import com.mineinabyss.launchy.ui.state.windowScope

var screen: Screen by mutableStateOf(Screen.Default)

var dialog: Dialog by mutableStateOf(Dialog.None)

var progress: Progress by mutableStateOf(Progress.Animated)

private val ModpackStateProvider = compositionLocalOf<ModpackState> { error("No local modpack provided") }

val LocalModpackState: ModpackState
    @Composable get() = ModpackStateProvider.current

@Composable
fun Screens() {
    val state = LocalLaunchyState
    val packState = state.modpackState
    if (packState != null) CompositionLocalProvider(ModpackStateProvider provides packState) {
        TransitionFade(screen is Screen.Modpack) {
            ModpackScreen()
        }
        TranslucentTopBar(screen) {
            TransitionSlideUp(screen == Screen.Settings) {
                SettingsScreen()
            }
        }
    }

    TranslucentTopBar(screen) {
        TransitionFade(screen == Screen.Default) {
            HomeScreen()
        }
    }

    AppTopBar(
        TopBar,
        screen.transparentTopBar,
        showBackButton = screen != Screen.Default,
        onBackButtonClicked = {
            screen = when (screen) {
                Screen.Modpack -> Screen.Default
                Screen.Settings -> Screen.Modpack
                else -> Screen.Default
            }
        }
    )

    when (val castDialog = dialog) {
        Dialog.None -> {}
        Dialog.Auth -> AuthDialog(
            windowScope,
            onDismissRequest = { dialog = Dialog.None },
        )

        is Dialog.Error -> LaunchyDialog(
            title = { Text(castDialog.title, style = LocalTextStyle.current) },
            content = { Text(castDialog.message, style = LocalTextStyle.current) },
            windowScope,
            { dialog = Dialog.None },
            { dialog = Dialog.None },
            "Close",
            null,
        )
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

