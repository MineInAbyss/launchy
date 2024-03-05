package com.mineinabyss.launchy.ui.screens.modpack.main

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.ui.screens.LocalModpackState
import com.mineinabyss.launchy.ui.screens.Progress
import com.mineinabyss.launchy.ui.screens.modpack.main.buttons.PlayButton
import com.mineinabyss.launchy.ui.screens.modpack.main.buttons.SettingsButton
import com.mineinabyss.launchy.ui.screens.progress
import com.mineinabyss.launchy.ui.state.windowScope

@ExperimentalComposeUiApi
@Preview
@Composable
fun ModpackScreen() {
    val state = LocalLaunchyState
    val packState = LocalModpackState

    Box {
        BackgroundImage(windowScope)

        Column(
            modifier =
            Modifier.align(Alignment.Center)
                .heightIn(0.dp, 550.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LogoLarge(Modifier.weight(3f))
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().weight(1f),
            ) {
//                AuthButton()
                PlayButton(hideText = false, packState.modpack.info) { packState }
//                InstallButton(!state.isDownloading && state.operationsQueued)
//                AnimatedVisibility(state.operationsQueued) {
//                    UpdateInfoButton()
//                }
//                Spacer(Modifier.width(10.dp))
                SettingsButton()
            }
        }
        FirstLaunchDialog()
        HandleImportSettings()

        val isDownloading = packState.downloads.isDownloading
        LaunchedEffect(isDownloading) {
            progress = if (isDownloading) Progress.Animated else Progress.None
        }

        val tasks = packState.downloads.inProgressTasks
        if (tasks.isNotEmpty()) {
                val task = tasks.values.first()
                Text("Installing ${task.name}...", modifier = Modifier.align(Alignment.BottomStart).padding(start = 10.dp, bottom = 20.dp))
        }

        when (progress) {
            Progress.Animated -> {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
                    color = MaterialTheme.colorScheme.primaryContainer
                )
            }

            else -> {}
        }
    }
}
