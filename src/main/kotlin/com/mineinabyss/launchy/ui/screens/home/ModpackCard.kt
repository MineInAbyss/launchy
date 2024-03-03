package com.mineinabyss.launchy.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
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
import com.mineinabyss.launchy.data.Dirs
import com.mineinabyss.launchy.data.config.ModpackUserConfig
import com.mineinabyss.launchy.data.modpacks.ModpackInfo
import com.mineinabyss.launchy.state.modpack.ModpackState
import com.mineinabyss.launchy.ui.colors.currentHue
import com.mineinabyss.launchy.ui.screens.Dialog
import com.mineinabyss.launchy.ui.screens.Screen
import com.mineinabyss.launchy.ui.screens.dialog
import com.mineinabyss.launchy.ui.screens.modpack.main.SlightBackgroundTint
import com.mineinabyss.launchy.ui.screens.screen
import kotlinx.coroutines.launch
import kotlin.io.path.Path

@Composable
fun ModpackCard(pack: ModpackInfo) {
    val state = LocalLaunchyState
    val cardHeight = 256.dp
    val cardPadding = 12.dp
    val coroutineScope = rememberCoroutineScope()
    Card(
        onClick = {
            coroutineScope.launch {
                val userConfig = ModpackUserConfig()
                val modpackDir = userConfig.modpackMinecraftDir?.let { Path(it) } ?: Dirs.modpackDir(pack.folderName)
                val modpack = pack.source.getOrDownloadLatestPack(pack, modpackDir) ?: run {
                    dialog = Dialog.Error("Failed to download modpack", "")
                    return@launch
                }
                state.modpackState = ModpackState(modpackDir, modpack, userConfig)
                currentHue = pack.hue
                screen = Screen.Modpack
            }
        },
        modifier = Modifier.width(400.dp).height(cardHeight)
    ) {
        val background by produceState<BitmapPainter?>(null) {
            value = BitmapPainter(pack.getOrDownloadBackground())
        }
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
                FloatingActionButton(
                    onClick = {},
                    modifier = Modifier
                ) {
                    Icon(Icons.Rounded.PlayArrow, contentDescription = "Play")
                }
            }
        }
    }
}
