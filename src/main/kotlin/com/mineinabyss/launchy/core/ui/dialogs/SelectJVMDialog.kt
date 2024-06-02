package com.mineinabyss.launchy.core.ui.dialogs

import androidx.compose.material.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.mineinabyss.launchy.core.ui.Dialog
import com.mineinabyss.launchy.core.ui.components.LaunchyDialog
import com.mineinabyss.launchy.core.ui.screens.Screen
import com.mineinabyss.launchy.core.ui.screens.dialog
import com.mineinabyss.launchy.core.ui.screens.screen
import com.mineinabyss.launchy.settings.ui.JVMSettingsViewModel
import com.mineinabyss.launchy.util.koinViewModel

@Composable
fun SelectJVMDialog(
    jvm: JVMSettingsViewModel = koinViewModel()
) {
    LaunchyDialog(
        title = { Text("Install java", style = LocalTextStyle.current) },
        onAccept = {
            dialog = Dialog.None
            runCatching {
                jvm.installJDK()
            }.getOrElse {
                dialog = Dialog.Error(
                    "Failed to install Java",
                    it.stackTraceToString()
                )
                return@LaunchyDialog
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
