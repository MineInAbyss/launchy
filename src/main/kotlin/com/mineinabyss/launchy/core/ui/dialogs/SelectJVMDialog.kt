package com.mineinabyss.launchy.core.ui.dialogs

import androidx.compose.material.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.core.data.Downloader
import com.mineinabyss.launchy.core.ui.Dialog
import com.mineinabyss.launchy.core.ui.Screen
import com.mineinabyss.launchy.core.ui.components.LaunchyDialog
import com.mineinabyss.launchy.core.ui.dialog
import com.mineinabyss.launchy.core.ui.screen
import com.mineinabyss.launchy.util.AppDispatchers
import kotlinx.coroutines.launch

@Composable
fun SelectJVMDialog() {
    val coroutineScope = rememberCoroutineScope()
    val state = LocalLaunchyState
    LaunchyDialog(
        title = { Text("Install java", style = LocalTextStyle.current) },
        onAccept = {
            dialog = Dialog.None
            AppDispatchers.IO.launch {
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
