package com.mineinabyss.launchy.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.logic.AppUpdateState
import com.mineinabyss.launchy.logic.DesktopHelpers
import com.mineinabyss.launchy.logic.GithubUpdateChecker
import com.mineinabyss.launchy.state.InProgressTask
import com.mineinabyss.launchy.state.modpack.GameInstanceState
import com.mineinabyss.launchy.ui.AppTopBar
import com.mineinabyss.launchy.ui.colors.currentHue
import com.mineinabyss.launchy.ui.dialogs.AuthDialog
import com.mineinabyss.launchy.ui.dialogs.SelectJVMDialog
import com.mineinabyss.launchy.ui.elements.LaunchyDialog
import com.mineinabyss.launchy.ui.screens.home.HomeScreen
import com.mineinabyss.launchy.ui.screens.home.newinstance.NewInstance
import com.mineinabyss.launchy.ui.screens.home.settings.SettingsScreen
import com.mineinabyss.launchy.ui.screens.modpack.main.InstanceScreen
import com.mineinabyss.launchy.ui.screens.modpack.main.SlightBackgroundTint
import com.mineinabyss.launchy.ui.screens.modpack.settings.InfoBarProperties
import com.mineinabyss.launchy.ui.screens.modpack.settings.InstanceSettingsScreen
import com.mineinabyss.launchy.ui.state.TopBar

var screen: Screen by mutableStateOf(Screen.Default)

var dialog: Dialog by mutableStateOf(Dialog.None)
var updateAvailable: AppUpdateState by mutableStateOf(AppUpdateState.Unknown)

private val ModpackStateProvider = compositionLocalOf<GameInstanceState> { error("No local modpack provided") }

val snackbarHostState = SnackbarHostState()

val LocalGameInstanceState: GameInstanceState
    @Composable get() = ModpackStateProvider.current

@Composable
fun Screens() = Scaffold(
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
    val state = LocalLaunchyState
    val packState = state.instanceState

    if (packState != null) CompositionLocalProvider(ModpackStateProvider provides packState) {
        Screen(Screen.Instance) { InstanceScreen() }
        Screen(Screen.InstanceSettings, transition = Transitions.SlideUp) { InstanceSettingsScreen() }
    }
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

    LaunchedEffect(isDefault, state.ui.preferHue) {
        if (isDefault) currentHue = state.ui.preferHue
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
                    packState?.saveToConfig()
                    Screen.Default
                }

                Screen.InstanceSettings -> {
                    packState?.saveToConfig()
                    Screen.Instance
                }

                Screen.Settings -> {
                    state.saveToConfig()
                    Screen.Default
                }

                else -> Screen.Default
            }
        }
    )

    when (val castDialog = dialog) {
        Dialog.None -> {}
        Dialog.Auth -> AuthDialog(
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

    val tasks = state.inProgressTasks
    val progressBarHeight by animateDpAsState(if (screen == Screen.InstanceSettings) InfoBarProperties.height else 0.dp)

    if (tasks.isNotEmpty()) Box(Modifier.fillMaxSize().padding(bottom = progressBarHeight)) {
        val task = tasks.values.first()
        val textModifier = Modifier.align(Alignment.BottomStart).padding(start = 10.dp, bottom = 20.dp)
        val progressBarModifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter)
        val progressBarColor = MaterialTheme.colorScheme.primaryContainer
        SlightBackgroundTint(Modifier.height(50.dp))
        when (task) {
            is InProgressTask.WithPercentage -> {
                Text(
                    "${task.name}... (${task.current}/${task.total}${if (task.measurement != null) " ${task.measurement}" else ""})",
                    modifier = textModifier
                )
                LinearProgressIndicator(
                    progress = task.current.toFloat() / task.total,
                    modifier = progressBarModifier,
                    color = progressBarColor
                )
            }

            else -> {
                Text(
                    "${task.name}...",
                    modifier = textModifier
                )

                LinearProgressIndicator(
                    modifier = progressBarModifier,
                    color = progressBarColor
                )
            }
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
}
