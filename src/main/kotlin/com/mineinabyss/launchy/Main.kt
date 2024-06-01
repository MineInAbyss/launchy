package com.mineinabyss.launchy

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.mineinabyss.launchy.config.data.configModule
import com.mineinabyss.launchy.core.di.coreModule
import com.mineinabyss.launchy.core.ui.*
import com.mineinabyss.launchy.core.ui.screens.Screens
import com.mineinabyss.launchy.core.ui.theme.AppTheme
import com.mineinabyss.launchy.util.koinViewModel
import org.koin.compose.KoinApplication
import java.awt.Dimension

fun main() = application {
    KoinApplication(application = {
        modules(
            coreModule(),
            configModule(),
        )
    }) {
        val windowState = rememberWindowState(placement = WindowPlacement.Floating)
        val icon = painterResource("icon.png")
        val viewModel = koinViewModel<LaunchyViewModel>()
        val onClose: () -> Unit = {
            exitApplication()
            viewModel.saveToConfig()
        }

        Window(
            state = windowState,
            title = "Launchy",
            icon = icon,
            onCloseRequest = onClose,
            undecorated = true,
        ) {
            window.minimumSize = Dimension(600, 400)
            val topBarState = remember { TopBarState(onClose, windowState, this) }
            val uiState by viewModel.uiState.collectAsState()
            AppTheme {
                CompositionLocalProvider(TopBarProvider provides topBarState) {
                    Scaffold {
                        AnimatedContent(uiState) {
                            when (val state = uiState) {
                                LaunchyUiState.Loading ->
                                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator()
                                    }

                                is LaunchyUiState.Ready ->
                                    CompositionLocalProvider(
                                        LocalUiState provides state.ui
                                    ) {
                                        Screens()
                                    }
                            }
                        }
                        AnimatedVisibility(uiState is LaunchyUiState.Loading, exit = fadeOut()) {
                        }
                        AnimatedVisibility(uiState is LaunchyUiState.Ready, enter = fadeIn()) {
                        }
                    }
                }
            }
        }
    }
}
