package com.mineinabyss.launchy.instance.ui.components.buttons

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.core.ui.components.PrimaryButton
import com.mineinabyss.launchy.instance.ui.InstallState

@Composable
fun InstallButton(
    state: InstallState,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    PrimaryButton(
        enabled = state is InstallState.Queued,
        onClick = onClick,
        modifier = modifier.width(150.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Download, "Download")
            AnimatedVisibility(true, Modifier.animateContentSize()) {
                val text = when (state) {
                    is InstallState.Queued, InstallState.Error -> "Install"
                    InstallState.AllInstalled -> "Installed"
                    InstallState.InProgress -> "Installing"
                }
                AnimatedContent(text) {
                    Text(it)
                }
            }
        }
    }
}

@Composable
fun InstallTextAnimatedVisibility(visible: Boolean, content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = shrinkHorizontally(shrinkTowards = Alignment.Start) + fadeOut()
    ) {
        content()
    }
}
