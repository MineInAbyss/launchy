package com.mineinabyss.launchy.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.mineinabyss.launchy.data.config.GameInstance
import com.mineinabyss.launchy.ui.colors.LaunchyColors
import com.mineinabyss.launchy.ui.colors.currentHue
import com.mineinabyss.launchy.ui.elements.Tooltip
import com.mineinabyss.launchy.ui.screens.Screen
import com.mineinabyss.launchy.ui.screens.home.ModpackCardStyle.cardHeight
import com.mineinabyss.launchy.ui.screens.home.ModpackCardStyle.cardPadding
import com.mineinabyss.launchy.ui.screens.home.ModpackCardStyle.cardWidth
import com.mineinabyss.launchy.ui.screens.modpack.main.SlightBackgroundTint
import com.mineinabyss.launchy.ui.screens.modpack.main.buttons.PlayButton
import com.mineinabyss.launchy.ui.screens.screen
import kotlinx.coroutines.launch

object ModpackCardStyle {
    val cardHeight = 256.dp
    val cardPadding = 12.dp
    val cardWidth = 400.dp
}

@Composable
fun ModpackCard(instance: GameInstance) = MaterialTheme(
    colorScheme = LaunchyColors(instance.config.hue).DarkColors
) {
    val state = LocalLaunchyState
    val coroutineScope = rememberCoroutineScope()
    val background by produceState<BitmapPainter?>(null) {
        value = instance.getOrDownloadBackground()
    }
    Card(
        onClick = {
            coroutineScope.launch {
                state.modpackState = instance.createModpackState()
                currentHue = instance.config.hue
                screen = Screen.Modpack
            }
        },
        modifier = Modifier.height(cardHeight).width(cardWidth),
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
            if (instance.config.cloudInstanceURL != null) TooltipArea(
                tooltip = { Tooltip("Cloud modpack") },
                modifier = Modifier.align(Alignment.TopEnd).padding(cardPadding + 4.dp).size(24.dp),
            ) {
                Icon(
                    Icons.Rounded.Cloud,
                    "Cloud modpack",
                )
            }
            Row(
                Modifier.align(Alignment.BottomStart).padding(cardPadding),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column {
                    Text(instance.config.name, style = MaterialTheme.typography.headlineMedium)
                    Text(instance.config.description, style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(Modifier.weight(1f))
                PlayButton(hideText = true, instance) { instance.createModpackState() }
            }
        }
    }
}
