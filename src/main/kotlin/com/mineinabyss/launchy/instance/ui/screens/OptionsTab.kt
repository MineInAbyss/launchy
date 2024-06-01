package com.mineinabyss.launchy.instance.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.core.ui.LocalGameInstanceState
import com.mineinabyss.launchy.core.ui.components.ComfyContent
import com.mineinabyss.launchy.core.ui.components.TitleSmall
import com.mineinabyss.launchy.core.ui.screens.Screen
import com.mineinabyss.launchy.core.ui.screens.screen
import com.mineinabyss.launchy.util.AppDispatchers
import com.mineinabyss.launchy.util.DesktopHelpers
import com.mineinabyss.launchy.util.InProgressTask
import kotlinx.coroutines.launch

@Composable
fun OptionsTab() {
    val state = LocalLaunchyState
    val pack = LocalGameInstanceState

    ComfyContent(Modifier.padding(16.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            TitleSmall("Mods")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { pack.instance.updateInstance(state) }) {
                    Text("Force update Instance")
                }
                OutlinedButton(onClick = { DesktopHelpers.openDirectory(pack.instance.minecraftDir) }) {
                    Text("Open .minecraft folder")
                }
                OutlinedButton(onClick = {
                    AppDispatchers.IO.launch {
                        state.runTask("checkHashes", InProgressTask("Checking hashes")) {
                            pack.checkHashes(pack.queued.modDownloadInfo).forEach { (modId, newInfo) ->
                                pack.queued.modDownloadInfo[modId] = newInfo
                            }
                        }
                    }
                }) {
                    Text("Re-check hashes")
                }
            }

            TitleSmall("Danger zone")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = {
                    screen = Screen.Default
                    pack.instance.delete(state, deleteDotMinecraft = false)
                }) {
                    Text("Delete Instance from config")
                }
                OutlinedButton(
                    onClick = {
                        screen = Screen.Default
                        pack.instance.delete(state, deleteDotMinecraft = true)
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    )
                ) {
                    Text("Delete Instance and its .minecraft")
                }
            }
        }
    }
}
