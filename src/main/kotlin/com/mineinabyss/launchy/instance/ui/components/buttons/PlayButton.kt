package com.mineinabyss.launchy.instance.ui.components.buttons

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.PlayDisabled
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.core.ui.components.PrimaryButtonColors
import com.mineinabyss.launchy.core.ui.components.SecondaryButtonColors
import com.mineinabyss.launchy.instance.ui.GameInstanceState
import com.mineinabyss.launchy.instance.ui.InstanceUiState
import com.mineinabyss.launchy.launcher.ui.LauncherViewModel
import com.mineinabyss.launchy.util.koinViewModel

@Composable
fun PlayButton(
    hideText: Boolean = false,
    instance: InstanceUiState,
    modifier: Modifier = Modifier,
    launcher: LauncherViewModel = koinViewModel()
) {
    val buttonIcon by remember(instance, instance.runningProcess) {
        mutableStateOf(
            when {
                state.profile.currentProfile == null -> Icons.Rounded.PlayDisabled
                process == null -> Icons.Rounded.PlayArrow
                else -> Icons.Rounded.Stop
            }
        )
    }
    val buttonText by remember(process) {
        mutableStateOf(if (process == null) "Play" else "Stop")
    }
    val buttonColors by mutableStateOf(
        if (process == null) PrimaryButtonColors
        else SecondaryButtonColors
    )

    Box {
        var foundPackState: GameInstanceState? by remember { mutableStateOf(null) }
        val onClick: () -> Unit = {
            launcher.launch(instance)
        }
        val enabled = state.profile.currentProfile != null
                && foundPackState?.downloads?.isDownloading != true
                && state.inProgressTasks.isEmpty()

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
