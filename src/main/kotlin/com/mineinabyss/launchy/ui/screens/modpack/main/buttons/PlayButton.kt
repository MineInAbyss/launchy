package com.mineinabyss.launchy.ui.screens.modpack.main.buttons

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.PlayDisabled
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.data.config.GameInstance
import com.mineinabyss.launchy.logic.AppDispatchers
import com.mineinabyss.launchy.logic.AppDispatchers.launchOrShowDialog
import com.mineinabyss.launchy.logic.Launcher
import com.mineinabyss.launchy.logic.ModDownloader.prepareWithoutChangingInstalledMods
import com.mineinabyss.launchy.logic.ModDownloader.startInstall
import com.mineinabyss.launchy.state.modpack.GameInstanceState
import com.mineinabyss.launchy.ui.elements.PrimaryButtonColors
import com.mineinabyss.launchy.ui.elements.SecondaryButtonColors
import com.mineinabyss.launchy.ui.screens.Dialog
import com.mineinabyss.launchy.ui.screens.dialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun PlayButton(
    hideText: Boolean = false,
    instance: GameInstance,
    modifier: Modifier = Modifier,
    getModpackState: suspend () -> GameInstanceState?,
) {
    val state = LocalLaunchyState
    val process = state.processFor(instance)
    val coroutineScope = rememberCoroutineScope()
    val buttonIcon by remember(state.profile.currentProfile, process) {
        mutableStateOf(
            when {
                state.profile.currentProfile == null -> Icons.Rounded.PlayDisabled
                process == null -> Icons.Rounded.PlayArrow
                else -> Icons.Rounded.Stop
            }
        )
    }
    val buttonText by remember(process) {
        mutableStateOf(if (process == null) "Play" else "Stop")
    }
    val buttonColors by mutableStateOf(
        if (process == null) PrimaryButtonColors
        else SecondaryButtonColors
    )

    Box {
        var foundPackState: GameInstanceState? by remember { mutableStateOf(null) }
        val onClick: () -> Unit = {
            coroutineScope.launch(Dispatchers.IO) {
                val packState = foundPackState ?: getModpackState() ?: return@launch
                foundPackState = packState
                val operationsQueued = packState.queued.areOperationsQueued

                if (process == null) {
                    when {
                        // Assume this means not launched before
                        packState.userAgreedModLoaders == null -> {
                            AppDispatchers.profileLaunch.launchOrShowDialog {
                                packState.startInstall(state)
                                Launcher.launch(state, packState, state.profile)
                            }
                        }

                        operationsQueued -> {
                            dialog = Dialog.Options(
                                title = "Install changes before launch?",
                                message = buildString {
                                    appendLine("This instance has changes that are not installed yet,")
                                    appendLine("would you like to apply these changes now?")
                                },
                                acceptText = "Install",
                                declineText = "Skip",
                                onAccept = {
                                    AppDispatchers.profileLaunch.launch {
                                        packState.startInstall(state)
                                        Launcher.launch(state, packState, state.profile)
                                    }
                                },
                                onDecline = {
                                    AppDispatchers.profileLaunch.launch {
                                        packState.prepareWithoutChangingInstalledMods(state)
                                        Launcher.launch(state, packState, state.profile)
                                    }
                                }
                            )
                        }

                        else -> {
                            AppDispatchers.profileLaunch.launchOrShowDialog {
                                packState.prepareWithoutChangingInstalledMods(state)
                                println("Launching now!")
                                Launcher.launch(state, packState, state.profile)
                            }
                        }
                    }
                } else {
                    process.destroyForcibly()
                    state.setProcessFor(packState.instance, null)
                }
            }
        }
        val enabled = state.profile.currentProfile != null
                && foundPackState?.downloads?.isDownloading != true
                && state.inProgressTasks.isEmpty()

        if (hideText) Button(
            enabled = enabled,
            onClick = onClick,
            modifier = Modifier.size(52.dp).then(modifier),
            contentPadding = PaddingValues(0.dp),
            colors = buttonColors,
            shape = MaterialTheme.shapes.medium
        ) {
            Icon(buttonIcon, buttonText)
        }
        else Button(
            enabled = enabled,
            onClick = onClick,
            shape = RoundedCornerShape(20.dp),
            colors = buttonColors
        ) {
            Icon(buttonIcon, buttonText)
            Text(buttonText)
        }
    }
}
