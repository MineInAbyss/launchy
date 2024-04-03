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
import com.mineinabyss.launchy.logic.Instances.updateInstance
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
                val updatesAvailable = packState.instance.updatesAvailable

                if (process == null) {
                    when {
                        // Assume this means not launched before
                        packState.queued.userAgreedModLoaders == null -> {
                            AppDispatchers.profileLaunch.launchOrShowDialog {
                                packState.startInstall(state)
                                Launcher.launch(state, packState, state.profile)
                            }
                        }

                        updatesAvailable -> {
                            dialog = Dialog.Options(
                                title = "Update Available",
                                message = buildString {
                                    appendLine("This cloud instance has updates available.")
                                    appendLine("Would you like to download them now?")
                                },
                                acceptText = "Download",
                                declineText = "Ignore",
                                onAccept = { packState.instance.updateInstance(state) },
                                onDecline = { }
                            )
                        }

                        else -> {
                            AppDispatchers.profileLaunch.launchOrShowDialog {
                                packState.startInstall(state)
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
