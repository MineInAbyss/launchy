package com.mineinabyss.launchy.instance.ui.components.settings.infobar

import androidx.compose.animation.*
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.core.ui.components.Tooltip

@Composable
fun ActionButton(shown: Boolean, icon: ImageVector, desc: String, count: Int? = null) {
    AnimatedVisibility(
        shown,
        enter = fadeIn() + expandHorizontally(expandFrom = Alignment.Start),
        exit = fadeOut() + shrinkHorizontally(shrinkTowards = Alignment.Start)
    ) {
        Row {
            TooltipArea(tooltip = { Tooltip(desc) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, desc, modifier = Modifier.padding(end = 4.dp).alignByBaseline())
                    if (count != null) {
                        val animatedCount by animateIntAsState(targetValue = count)
                        Text(animatedCount.toString(), modifier = Modifier.alignByBaseline())
                    }
                }
            }
        }
    }
}
