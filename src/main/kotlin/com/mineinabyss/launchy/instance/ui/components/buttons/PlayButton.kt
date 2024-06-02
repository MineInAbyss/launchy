package com.mineinabyss.launchy.instance.ui.components.buttons

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.core.ui.components.PrimaryButtonColors
import com.mineinabyss.launchy.core.ui.components.SecondaryButtonColors
import com.mineinabyss.launchy.instance.ui.InstanceUiState

@Composable
fun PlayButton(
    hideText: Boolean = false,
    instance: InstanceUiState,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val buttonIcon by remember(instance, instance.runningProcess) {
        mutableStateOf(
            when {
//                state.profile.currentProfile == null -> Icons.Rounded.PlayDisabled
                instance.runningProcess == null -> Icons.Rounded.PlayArrow
                else -> Icons.Rounded.Stop
            }
        )
    }
    val buttonText by remember(instance.runningProcess) {
        mutableStateOf(if (instance.runningProcess == null) "Play" else "Stop")
    }
    val buttonColors by mutableStateOf(
        if (instance.runningProcess == null) PrimaryButtonColors
        else SecondaryButtonColors
    )

    val enabled = instance.enabled

    Box {
        if (hideText) Button(
            enabled = enabled,
            onClick = onClick,
            modifier = Modifier.size(52.dp).then(modifier),
            contentPadding = PaddingValues(0.dp),
            colors = buttonColors,
            shape = MaterialTheme.shapes.medium
        ) {
            Icon(buttonIcon, buttonText)
        }
        else Button(
            enabled = enabled,
            onClick = onClick,
            shape = RoundedCornerShape(20.dp),
            colors = buttonColors
        ) {
            Icon(buttonIcon, buttonText)
            Text(buttonText)
        }
    }
}
