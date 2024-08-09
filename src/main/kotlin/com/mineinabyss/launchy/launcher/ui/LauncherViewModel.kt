package com.mineinabyss.launchy.launcher.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mineinabyss.launchy.core.ui.Dialog
import com.mineinabyss.launchy.core.ui.screens.dialog
import com.mineinabyss.launchy.downloads.data.ModDownloader.startInstall
import com.mineinabyss.launchy.instance.ui.InstanceUiState
import com.mineinabyss.launchy.launcher.data.Launcher
import com.mineinabyss.launchy.util.AppDispatchers
import com.mineinabyss.launchy.util.AppDispatchers.launchOrShowDialog
import kotlinx.coroutines.launch

class LauncherViewModel : ViewModel() {
    fun launch(instance: InstanceUiState) {
        viewModelScope.launch(AppDispatchers.IO) {
            val packState = foundPackState ?: getModpackState() ?: return@launch
            foundPackState = packState
            val updatesAvailable = packState.instance.updatesAvailable

            if (process == null) {
                when {
                    // Assume this means not launched before
                    packState.queued.userAgreedModLoaders == null -> {
                        AppDispatchers.profileLaunch.launchOrShowDialog {
                            packState.startInstall(state).getOrThrow()
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
                            packState.startInstall(state).getOrThrow()
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
}
