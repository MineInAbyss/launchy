package com.mineinabyss.launchy.ui.screens.main

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.data.Dirs
import com.mineinabyss.launchy.logic.Auth
import com.mineinabyss.launchy.logic.Downloader
import com.mineinabyss.launchy.ui.auth.AuthDialog
import com.mineinabyss.launchy.ui.screens.Dialog
import com.mineinabyss.launchy.ui.screens.Progress
import com.mineinabyss.launchy.ui.screens.dialog
import com.mineinabyss.launchy.ui.screens.main.buttons.AuthButton
import com.mineinabyss.launchy.ui.screens.main.buttons.PlayButton
import com.mineinabyss.launchy.ui.screens.main.buttons.SettingsButton
import com.mineinabyss.launchy.ui.screens.progress
import com.mineinabyss.launchy.ui.state.windowScope
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.inputStream

@ExperimentalComposeUiApi
@Preview
@Composable
fun MainScreen() {
    val state = LocalLaunchyState

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
                AuthButton()
                PlayButton()
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

        LaunchedEffect(state.isDownloading) {
            progress = if (state.isDownloading) Progress.Animated else Progress.None
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

        if (Auth.hasPreviousSession(state)) AuthDialog(windowScope, { dialog = Dialog.None }, { dialog = Dialog.None })
        LaunchedEffect(state.currentSession) {
            val uuid = state.currentSession?.auth()?.uuid ?: return@LaunchedEffect
            val avatarPath = Dirs.avatars / "$uuid.png"
            if (!avatarPath.exists()) Downloader.downloadAvatar(uuid)
            state.avatar = loadImageBitmap(avatarPath.inputStream())
        }
        val fabSize = 64.dp

        FloatingActionButton(
            onClick = { },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp).size(fabSize),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.primary,
        ) {
            state.avatar?.let {
                Image(
                    painter = BitmapPainter(it),
                    contentDescription = "Avatar",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
