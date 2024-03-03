package com.mineinabyss.launchy.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.data.modpacks.ModpackInfo
import com.mineinabyss.launchy.logic.Launcher
import com.mineinabyss.launchy.ui.colors.LaunchyColors
import com.mineinabyss.launchy.ui.colors.currentHue
import com.mineinabyss.launchy.ui.screens.Screen
import com.mineinabyss.launchy.ui.screens.home.ModpackCardStyle.cardHeight
import com.mineinabyss.launchy.ui.screens.home.ModpackCardStyle.cardPadding
import com.mineinabyss.launchy.ui.screens.home.ModpackCardStyle.cardWidth
import com.mineinabyss.launchy.ui.screens.modpack.main.SlightBackgroundTint
import com.mineinabyss.launchy.ui.screens.screen
import kotlinx.coroutines.launch

object ModpackCardStyle {
    val cardHeight = 256.dp
    val cardPadding = 12.dp
    val cardWidth = 400.dp
}

@Composable
fun AddNewModpackCard() {
    val highlightColor = MaterialTheme.colorScheme.secondary
    Surface(
        border = BorderStroke(3.dp, highlightColor),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.width(cardWidth).height(cardHeight).clickable {}
    ) {
        Box {
            Row(Modifier.align(Alignment.Center), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Rounded.Add,
                    "Add modpack",
                    Modifier.size(40.dp),
                    tint = highlightColor
                )
            }
        }
    }
}

@Composable
fun ModpackCard(pack: ModpackInfo) = MaterialTheme(
    colorScheme = LaunchyColors(pack.hue).DarkColors
) {
    val state = LocalLaunchyState
    val coroutineScope = rememberCoroutineScope()
    val background by produceState<BitmapPainter?>(null) {
        value = BitmapPainter(pack.getOrDownloadBackground())
    }
    Card(
        onClick = {
            coroutineScope.launch {
                state.modpackState = pack.createModpackState()
                currentHue = pack.hue
                screen = Screen.Modpack
            }
        },
        modifier = Modifier.width(cardWidth).height(cardHeight)
    ) {
        Box {
            if (background != null) {
                Image(
                    painter = background!!,
                    contentDescription = "Pack background image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                SlightBackgroundTint()
            }
            Row(
                Modifier.align(Alignment.BottomStart).padding(cardPadding),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column {
                    Text(pack.name, style = MaterialTheme.typography.headlineMedium)
                    Text(pack.desc, style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(Modifier.weight(1f))

                val (containerColor, contentColor) = (state.profile.currentProfile?.let {
                    FloatingActionButtonDefaults.containerColor
                } ?: MaterialTheme.colorScheme.background).let { it to contentColorFor(it) }

                FloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            if (state.profile.currentProfile != null)
                                pack.createModpackState()?.let { Launcher.launch(it, state.profile) }
                        }
                    },
                    containerColor = containerColor,
                    contentColor = contentColor,
                    modifier = Modifier
                ) {
                    Icon(Icons.Rounded.PlayArrow, contentDescription = "Play")
                }
            }
        }
    }
}
