package com.mineinabyss.launchy.ui.screens.modpack.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.ui.screens.LocalGameInstanceState
import com.mineinabyss.launchy.ui.screens.modpack.main.buttons.PlayButton
import com.mineinabyss.launchy.ui.screens.modpack.main.buttons.SettingsButton
import com.mineinabyss.launchy.ui.screens.modpack.main.buttons.UpdateButton
import com.mineinabyss.launchy.ui.state.windowScope

@ExperimentalComposeUiApi
@Preview
@Composable
fun InstanceScreen() {
    val packState = LocalGameInstanceState

    Box {
        BackgroundImage(windowScope)

        Column(
            modifier =
            Modifier.align(Alignment.Center)
                .heightIn(0.dp, 550.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LogoLarge(Modifier.weight(3f, false))
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().weight(1f, false),
            ) {
                PlayButton(hideText = false, packState.instance) { packState }
                AnimatedVisibility(packState.instance.updatesAvailable) {
                    UpdateButton()
                }
                SettingsButton()
            }
        }
        FirstLaunchDialog()
//        HandleImportSettings()
    }
}
