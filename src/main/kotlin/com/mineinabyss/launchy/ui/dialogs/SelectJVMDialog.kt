package com.mineinabyss.launchy.ui.dialogs

import androidx.compose.material.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.logic.Downloader
import com.mineinabyss.launchy.ui.elements.LaunchyDialog
import com.mineinabyss.launchy.ui.screens.Dialog
import com.mineinabyss.launchy.ui.screens.Screen
import com.mineinabyss.launchy.ui.screens.dialog
import com.mineinabyss.launchy.ui.screens.screen
import kotlinx.coroutines.launch

@Composable
fun SelectJVMDialog() {
    val coroutineScope = rememberCoroutineScope()
    val state = LocalLaunchyState
    LaunchyDialog(
        title = { Text("Install java", style = LocalTextStyle.current) },
        onAccept = {
            dialog = Dialog.None
            state.ioScope.launch {
                val jdkPath = runCatching {
                    Downloader.installJDK(state)
                }.getOrElse {
                    dialog = Dialog.Error(
                        "Failed to install Java",
                        it.stackTraceToString()
                    )
                    return@launch
                }
                if (jdkPath != null) {
                    state.jvm.javaPath = jdkPath
                    state.saveToConfig()
                } else {
                    dialog = Dialog.Error(
                        "Failed to install Java",
                        "Please install Java manually and select the path in settings."
                    )
                }
            }
        },
        onDecline = { dialog = Dialog.None; screen = Screen.Settings },
        onDismiss = { dialog = Dialog.None; },
        acceptText = "Install automatically",
        declineText = "Choose manually",
    ) {
        Text("Launchy needs Java to run Minecraft. It can install a version for you, or you can choose a path to an existing installation.")
    }
}
