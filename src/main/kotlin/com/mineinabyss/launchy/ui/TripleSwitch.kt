package com.mineinabyss.launchy.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.data.Group
import com.mineinabyss.launchy.data.Mod
import com.mineinabyss.launchy.util.Option

@Composable
fun ToggleButtons(
    onSwitch: (Option) -> Unit,
    group: Group,
    mods: Collection<Mod>,
) {
    val state = LocalLaunchyState
    val offColor = MaterialTheme.colors.surface
    val forced = group.forced

    Card {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.width(140.dp)
        ) {
            val fullEnable = state.enabledMods.containsAll(mods)
            val fullDisable = mods.none { it in state.enabledMods }

            val disableColor by animateColorAsState(
                if (fullDisable) MaterialTheme.colors.error
                else offColor,
            )
            if (!forced)
                TripleSwitchButton(Option.DISABLED, disableColor, onSwitch, true, Modifier.weight(1f)) {
                    Icon(Icons.Rounded.Close, "Disabled")
                }

            val enableColor by animateColorAsState(
                if (fullEnable) MaterialTheme.colors.primary
                else if (!fullDisable) MaterialTheme.colors.primary.copy(alpha = 0.6f)
                else offColor
//                spring(
//                    dampingRatio = Spring.DampingRatioNoBouncy,
//                    stiffness = Spring.StiffnessVeryLow
//                ),
            )
            TripleSwitchButton(
                Option.ENABLED,
                enableColor,
                onSwitch,
                !forced,
                Modifier.weight(1f)
            ) {
                Icon(Icons.Rounded.Check, "Enabled")
            }
        }
    }
}

@Composable
fun TripleSwitchButton(
    setTo: Option,
    enabledColor: Color,
    onSwitch: (Option) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Button(
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(backgroundColor = enabledColor),
        shape = RectangleShape,
        onClick = { onSwitch(setTo) },
        modifier = modifier.fillMaxHeight()
    ) {
        content()
    }
}
