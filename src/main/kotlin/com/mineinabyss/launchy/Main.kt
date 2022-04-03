package com.mineinabyss.launchy

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CropSquare
import androidx.compose.material.icons.rounded.Minimize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import com.mineinabyss.launchy.data.Config
import com.mineinabyss.launchy.data.Dirs
import com.mineinabyss.launchy.data.Versions
import com.mineinabyss.launchy.logic.LaunchyState
import com.mineinabyss.launchy.ui.screens.MainScreen
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.memberProperties

//private val LocalConfigProvider = compositionLocalOf<Config> { error("No local config provided") }
//val LocalConfig: Config
//    @Composable
//    get() = LocalConfigProvider.current
//
//private val LocalVersionsProvider = compositionLocalOf<Versions> { error("No local versions provided") }
//val LocalVersions: Versions
//    @Composable
//    get() = LocalVersionsProvider.current
private val LaunchyStateProvider = compositionLocalOf<LaunchyState> { error("No local versions provided") }
val LocalLaunchyState: LaunchyState
    @Composable
    get() = LaunchyStateProvider.current

@Composable
fun WindowScope.AppWindowTitleBar(
    app: ApplicationScope,
    state: WindowState,
    onCloseRequest: () -> Unit,
) = WindowDraggableArea {
    Surface(
        Modifier.fillMaxWidth().height(40.dp),
        tonalElevation = 1.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            Spacer(Modifier.width(15.dp))
            Row(
                Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Mine in Abyss",
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Row {
                WindowButton(Icons.Rounded.Minimize) {
                    state.isMinimized = true
                }
                Spacer(Modifier.width(5.dp))
                WindowButton(Icons.Rounded.CropSquare) {
                    if(state.placement != WindowPlacement.Maximized)
                        state.placement = WindowPlacement.Maximized
                    else state.placement = WindowPlacement.Floating
                }
                Spacer(Modifier.width(5.dp))
                WindowButton(Icons.Rounded.Close) {
                    onCloseRequest()
                    app.exitApplication()
                }
            }
            Spacer(Modifier.width(5.dp))
        }
    }
}

@Composable
fun WindowButton(icon: ImageVector, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        Modifier.size(24.dp),
        contentPadding = PaddingValues(2.dp),
        colors = ButtonDefaults.outlinedButtonColors()
    ) {
        Icon(icon, "")
    }

}

fun main() {
    application {
        val windowState = rememberWindowState(placement = WindowPlacement.Floating)
        val icon = painterResource("mia_profile_icon.png")
//        val scaffoldState = rememberScaffoldState()
        val launchyState by produceState<LaunchyState?>(null) {
            val config = Config.read()
            val versions = Versions.readLatest(config.downloadUpdates)
            value = LaunchyState(config, versions/*, scaffoldState*/)
        }
        val onClose: () -> Unit = {
            exitApplication()
            launchyState?.save()
        }
        Window(
            state = windowState,
            title = "Mine in Abyss - Launcher",
            icon = icon,
            onCloseRequest = onClose,
            undecorated = true,
        ) {

            val ready = launchyState != null
            val scheme = darkColorScheme()
            ColorScheme::class.memberProperties.filterIsInstance<KMutableProperty1<ColorScheme, Color>>().map { prop ->
                val col = (prop.get(scheme))
                val hsbVals = FloatArray(3)
                val javaCol = java.awt.Color(col.red, col.green, col.blue, col.alpha)
                java.awt.Color.RGBtoHSB(javaCol.red, javaCol.green, javaCol.blue, hsbVals)
                val shiftedColor = Color(java.awt.Color.HSBtoRGB(0.02f, hsbVals[1], hsbVals[2]))
                prop.set(scheme, col.copy(red = shiftedColor.red, blue = shiftedColor.blue, green = shiftedColor.green))
            }
            MaterialTheme(
                colorScheme = scheme
            ) {
                Scaffold(
                    topBar = {
                        AppWindowTitleBar(this@application, windowState, onClose)
                    }
                ) {
                    AnimatedVisibility(!ready, exit = fadeOut()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Getting latest plugin versions...")
                        }
                    }
                    AnimatedVisibility(ready, enter = fadeIn()) {
                        CompositionLocalProvider(
                            LaunchyStateProvider provides launchyState!!,
                        ) {
                            Dirs.createDirs()
                            MainScreen()
                        }
                    }
                }
            }
        }
    }
}
