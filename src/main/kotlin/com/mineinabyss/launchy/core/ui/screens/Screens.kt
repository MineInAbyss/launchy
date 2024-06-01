package com.mineinabyss.launchy.core.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.auth.ui.AuthDialog
import com.mineinabyss.launchy.core.ui.Dialog
import com.mineinabyss.launchy.core.ui.LocalUiState
import com.mineinabyss.launchy.core.ui.TopBar
import com.mineinabyss.launchy.core.ui.components.AppTopBar
import com.mineinabyss.launchy.core.ui.components.InProgressTasksIndicator
import com.mineinabyss.launchy.core.ui.components.LaunchyDialog
import com.mineinabyss.launchy.core.ui.components.LeftSidebar
import com.mineinabyss.launchy.core.ui.dialogs.SelectJVMDialog
import com.mineinabyss.launchy.core.ui.theme.currentHue
import com.mineinabyss.launchy.instance.ui.screens.InstanceScreen
import com.mineinabyss.launchy.instance.ui.screens.InstanceSettingsScreen
import com.mineinabyss.launchy.instance_creation.ui.NewInstance
import com.mineinabyss.launchy.instance_list.ui.HomeScreen
import com.mineinabyss.launchy.settings.ui.SettingsScreen
import com.mineinabyss.launchy.updater.data.AppUpdateState
import com.mineinabyss.launchy.updater.data.GithubUpdateChecker
import com.mineinabyss.launchy.util.DesktopHelpers

var screen: Screen by mutableStateOf(Screen.Default)

var dialog: Dialog by mutableStateOf(Dialog.None)
var updateAvailable: AppUpdateState by mutableStateOf(AppUpdateState.Unknown)

//private val ModpackStateProvider = compositionLocalOf<GameInstanceState> { error("No local modpack provided") }

val snackbarHostState = SnackbarHostState()

//val LocalGameInstanceState: GameInstanceState
//    @Composable get() = ModpackStateProvider.current

@Composable
fun Screens(
) = Scaffold(
    snackbarHost = { SnackbarHost(snackbarHostState) },
    floatingActionButton = {
        val update = updateAvailable
        Row {
            AnimatedVisibility(update is AppUpdateState.UpdateAvailable) {
                if (update !is AppUpdateState.UpdateAvailable) return@AnimatedVisibility
                ExtendedFloatingActionButton(
                    text = { Text("Update available") },
                    icon = { Icon(Icons.Rounded.Update, "") },
                    onClick = { DesktopHelpers.browse(update.release.html_url) },
                )
            }
        }
    }
) {
    val ui = LocalUiState.current
    Screen(Screen.Instance) { InstanceScreen() }
    Screen(Screen.InstanceSettings, transition = Transitions.SlideUp) { InstanceSettingsScreen() }
    Screen(Screen.Default) { HomeScreen() }
    Screen(Screen.NewInstance) { NewInstance() }
    Screen(Screen.Settings) { SettingsScreen() }
    AnimatedVisibility(
        screen.showSidebar,
        enter = slideInHorizontally(initialOffsetX = { -80 }) + fadeIn(),
        exit = slideOutHorizontally(targetOffsetX = { -80 }) + fadeOut()
    ) {
        LeftSidebar()
    }

    val isDefault = screen is Screen.OnLeftSidebar

    LaunchedEffect(isDefault, ui.preferHue) {
        if (isDefault) currentHue = ui.preferHue
    }
    LaunchedEffect(Unit) {
        updateAvailable = GithubUpdateChecker.checkForUpdates()
    }


    AppTopBar(
        state = TopBar,
        transparent = screen.transparentTopBar,
        showTitle = screen.showTitle,
        showBackButton = screen != Screen.Default,
        onBackButtonClicked = {
            screen = when (screen) {
                Screen.Instance -> {
                    //TODO save states
//                    packState?.saveToConfig()
                    Screen.Default
                }

                Screen.InstanceSettings -> {
//                    packState?.saveToConfig()
                    Screen.Instance
                }

                Screen.Settings -> {
//                    state.saveToConfig()
                    Screen.Default
                }

                else -> Screen.Default
            }
        }
    )
    when (val castDialog = dialog) {
        Dialog.None -> {}
        is Dialog.Auth -> AuthDialog(
            castDialog,
            onDismissRequest = { dialog = Dialog.None },
        )

        Dialog.ChooseJVMPath -> SelectJVMDialog()

        is Dialog.Error -> LaunchyDialog(
            title = { Text(castDialog.title, style = LocalTextStyle.current) },
            onAccept = { dialog = Dialog.None },
            onDecline = { dialog = Dialog.None },
            onDismiss = { dialog = Dialog.None },
            acceptText = "Close",
            declineText = null,
        ) { Text(castDialog.message, style = LocalTextStyle.current) }

        is Dialog.Options -> {
            LaunchyDialog(
                title = { Text(castDialog.title, style = LocalTextStyle.current) },
                onAccept = { castDialog.onAccept(); dialog = Dialog.None },
                onDecline = { castDialog.onDecline(); dialog = Dialog.None },
                onDismiss = { dialog = Dialog.None },
                acceptText = castDialog.acceptText,
                declineText = castDialog.declineText,
            ) { Text(castDialog.message, style = LocalTextStyle.current) }
        }
    }
}

enum class Transitions {
    FadeIn, SlideUp
}

@Composable
fun Screen(
    onScreen: Screen,
    transition: Transitions = Transitions.FadeIn,
    content: @Composable () -> Unit,
) {
    val enter = when (transition) {
        Transitions.FadeIn -> fadeIn()
        Transitions.SlideUp -> fadeIn() + slideIn(initialOffset = { IntOffset(0, 100) })
    }
    val exit = when (transition) {
        Transitions.FadeIn -> fadeOut()
        Transitions.SlideUp -> fadeOut() + slideOut(targetOffset = { IntOffset(0, 100) })
    }
    val topPadding = if (onScreen.showTitle) 40.dp else 0.dp
    val startPadding = if (onScreen.showSidebar) 80.dp else 0.dp
    AnimatedVisibility(screen == onScreen, enter = enter, exit = exit) {
        Column {
            Box(Modifier.padding(start = startPadding, top = topPadding)) {
                content()
            }
        }
    }
    InProgressTasksIndicator()
}
