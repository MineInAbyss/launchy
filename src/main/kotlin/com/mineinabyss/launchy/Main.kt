package com.mineinabyss.launchy

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.mineinabyss.launchy.data.Config
import com.mineinabyss.launchy.data.Dirs
import com.mineinabyss.launchy.data.Versions
import com.mineinabyss.launchy.ui.screens.MainScreen

private val LocalConfigProvider = compositionLocalOf<Config> { error("No local config provided") }
val LocalConfig: Config
    @Composable
    get() = LocalConfigProvider.current

private val LocalVersionsProvider = compositionLocalOf<Versions> { error("No local versions provided") }
val LocalVersions: Versions
    @Composable
    get() = LocalVersionsProvider.current

fun main() = application {
    val versions by produceState<Versions?>(null) { value = Versions.readLatest() }
    val config by produceState<Config?>(null) { value = Config.read() }

    Window(onCloseRequest = {
        exitApplication()
        config?.save()
    }) {
        val ready = versions != null && config != null
        MaterialTheme(colors = darkColors()) {
            Scaffold {
                AnimatedVisibility(!ready, exit = fadeOut()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Getting latest plugin versions...")
                    }
                }
                AnimatedVisibility(ready, enter = fadeIn()) {
                    CompositionLocalProvider(
                        LocalConfigProvider provides config!!,
                        LocalVersionsProvider provides versions!!
                    ) {
                        Dirs.createDirs()
                        MainScreen()
                    }
                }
            }
        }
    }
}
