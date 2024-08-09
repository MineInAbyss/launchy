package com.mineinabyss.launchy.instance.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.core.ui.windowScope
import com.mineinabyss.launchy.instance.ui.InstanceUiState
import com.mineinabyss.launchy.instance.ui.InstanceViewModel
import com.mineinabyss.launchy.instance.ui.components.BackgroundImage
import com.mineinabyss.launchy.instance.ui.components.LogoLarge
import com.mineinabyss.launchy.instance.ui.components.buttons.PlayButton
import com.mineinabyss.launchy.instance.ui.components.buttons.SettingsButton
import com.mineinabyss.launchy.instance.ui.components.buttons.UpdateButton
import com.mineinabyss.launchy.util.koinViewModel

@ExperimentalComposeUiApi
@Preview
@Composable
fun InstanceScreen(
    instance: InstanceUiState,
    viewModel: InstanceViewModel = koinViewModel(),
) {
    Box {
        BackgroundImage(instance.background, windowScope)

        Column(
            modifier =
            Modifier.align(Alignment.Center)
                .heightIn(0.dp, 550.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LogoLarge(instance.logo, Modifier.weight(3f, false))
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().weight(1f, false),
            ) {
                PlayButton(
                    hideText = false,
                    instance,
                    onClick = { viewModel.launch() },
                )
                AnimatedVisibility(instance.updatesAvailable) {
                    UpdateButton()
                }
                SettingsButton()
            }
        }
//        HandleImportSettings()
    }
}
