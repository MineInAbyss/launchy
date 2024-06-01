package com.mineinabyss.launchy.instance_list.ui.components

import androidx.compose.animation.fadeIn
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.core.ui.components.Tooltip
import com.mineinabyss.launchy.core.ui.screens.Screen
import com.mineinabyss.launchy.core.ui.screens.screen
import com.mineinabyss.launchy.core.ui.theme.LaunchyColors
import com.mineinabyss.launchy.core.ui.theme.currentHue
import com.mineinabyss.launchy.instance.data.GameInstanceDataSource
import com.mineinabyss.launchy.instance.data.storage.InstanceConfig
import com.mineinabyss.launchy.instance.ui.components.SlightBackgroundTint
import com.mineinabyss.launchy.instance.ui.components.buttons.PlayButton
import com.mineinabyss.launchy.instance_list.ui.components.InstanceCardStyle.cardHeight
import com.mineinabyss.launchy.instance_list.ui.components.InstanceCardStyle.cardPadding
import com.mineinabyss.launchy.instance_list.ui.components.InstanceCardStyle.cardWidth
import com.mineinabyss.launchy.util.InProgressTask
import kotlinx.coroutines.launch

object InstanceCardStyle {
    val cardHeight = 256.dp
    val cardPadding = 12.dp
    val cardWidth = 400.dp
}

@Composable
fun InstanceCard(
    config: InstanceConfig,
    instance: GameInstanceDataSource? = null,
    modifier: Modifier = Modifier
) = MaterialTheme(
    colorScheme = LaunchyColors(config.hue).DarkColors
) {
    val state = LocalLaunchyState
    val coroutineScope = rememberCoroutineScope()
    val background by remember(config) { config.getBackgroundAsState() }
    Card(
        onClick = {
            instance ?: return@Card
            coroutineScope.launch {
                state.instanceState = instance.createModpackState(state)
                currentHue = instance.config.hue
                screen = Screen.Instance
            }
        },
        enabled = instance?.enabled == true,
        modifier = modifier.height(cardHeight).width(cardWidth),
    ) {
        Box(Modifier.fillMaxSize()) {
            androidx.compose.animation.AnimatedVisibility(
                visible = background != null,
                enter = fadeIn(),
                modifier = Modifier.fillMaxSize()
            ) {
                if (background != null) Image(
                    painter = background!!,
                    colorFilter =
                    if (instance?.enabled == false) ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
                    else null,
                    contentDescription = "Pack background image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            SlightBackgroundTint()

            if (config.cloudInstanceURL != null) TooltipArea(
                tooltip = { Tooltip("Cloud modpack") },
                modifier = Modifier.align(Alignment.TopEnd).padding(cardPadding + 4.dp).size(24.dp),
            ) {
                Icon(Icons.Rounded.Cloud, "Cloud modpack")
            }

            Row(
                Modifier.align(Alignment.BottomStart).padding(cardPadding),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(Modifier.weight(1f, true)) {
                    Text(
                        config.name,
                        style = MaterialTheme.typography.headlineMedium,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                    Text(
                        config.description,
                        style = MaterialTheme.typography.bodyMedium,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }
                if (instance?.enabled == true)
                    PlayButton(hideText = true, instance, Modifier.weight(1f, false)) {
                        state.runTask("modpackState", InProgressTask("Checking for pack updates...")) {
                            instance.createModpackState(state, awaitUpdatesCheck = true)
                        }
                    }
            }
        }
    }
}
