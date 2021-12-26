package com.mineinabyss.launchy.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DisabledByDefault
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import com.mineinabyss.launchy.util.Option

@Composable
fun TripleSwitch(option: Option, onSwitch: (Option) -> Unit) {
    Card {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TripleSwitchButton(MaterialTheme.colors.error, option, Option.DISABLED, onSwitch) {
                Icon(Icons.Rounded.Close, "Disabled")
            }
            TripleSwitchButton(MaterialTheme.colors.surface, option, Option.DEFAULT, onSwitch) {
                Text("/")
            }
            TripleSwitchButton(MaterialTheme.colors.primary, option, Option.ENABLED, onSwitch) {
                Icon(Icons.Rounded.Check, "Enabled")
            }
        }
    }
}

@Composable
fun TripleSwitchButton(
    enabledColor: Color,
    option: Option,
    setTo: Option,
    onSwitch: (Option) -> Unit,
    content: @Composable () -> Unit
) {
    val on = option == setTo
    val bgColor by animateColorAsState(if (on) enabledColor else MaterialTheme.colors.surface)

    Button(
        colors = ButtonDefaults.buttonColors(backgroundColor = bgColor),
        shape = RectangleShape,
        onClick = { onSwitch(setTo) },
        modifier = Modifier.fillMaxHeight()
    ) {
        content()
    }
}
