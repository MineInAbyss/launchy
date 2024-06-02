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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.core.ui.components.Tooltip
import com.mineinabyss.launchy.core.ui.theme.LaunchyColors
import com.mineinabyss.launchy.instance.ui.InstanceUiState
import com.mineinabyss.launchy.instance.ui.components.SlightBackgroundTint
import com.mineinabyss.launchy.instance.ui.components.buttons.PlayButton
import com.mineinabyss.launchy.instance_list.data.InstanceCardInteractions
import com.mineinabyss.launchy.instance_list.ui.components.InstanceCardStyle.cardHeight
import com.mineinabyss.launchy.instance_list.ui.components.InstanceCardStyle.cardPadding
import com.mineinabyss.launchy.instance_list.ui.components.InstanceCardStyle.cardWidth

object InstanceCardStyle {
    val cardHeight = 256.dp
    val cardPadding = 12.dp
    val cardWidth = 400.dp
}

@Composable
fun InstanceCard(
    instance: InstanceUiState,
    interactions: InstanceCardInteractions,
    modifier: Modifier = Modifier
) = MaterialTheme(colorScheme = LaunchyColors(instance.hue).DarkColors) {
    Card(
        onClick = { interactions.onOpen() },
        enabled = instance.enabled,
        modifier = modifier.height(cardHeight).width(cardWidth),
    ) {
        Box(Modifier.fillMaxSize()) {
            androidx.compose.animation.AnimatedVisibility(
                visible = instance.background != null,
                enter = fadeIn(),
                modifier = Modifier.fillMaxSize()
            ) {
                if (instance.background != null) Image(
                    painter = instance.background,
                    colorFilter =
                    if (instance.enabled == false) ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
                    else null,
                    contentDescription = "Pack background image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            SlightBackgroundTint()

            if (instance.isCloudInstance) TooltipArea(
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
                        instance.title,
                        style = MaterialTheme.typography.headlineMedium,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                    Text(
                        instance.description,
                        style = MaterialTheme.typography.bodyMedium,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }
                if (instance.enabled)
                    PlayButton(
                        hideText = true,
                        instance,
                        Modifier.weight(1f, false),
                        onClick = {
                            interactions.onPlay()
                        }
                    )
            }
        }
    }
}
