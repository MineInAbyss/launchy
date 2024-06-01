package com.mineinabyss.launchy.instance.ui.components.buttons

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.core.ui.LocalGameInstanceState
import com.mineinabyss.launchy.core.ui.components.OutlinedRedButton
import com.mineinabyss.launchy.core.ui.components.PrimaryButton
import com.mineinabyss.launchy.downloads.data.ModDownloader.startInstall
import com.mineinabyss.launchy.instance.ui.InstallState
import com.mineinabyss.launchy.instance.ui.InstanceViewModel
import com.mineinabyss.launchy.util.AppDispatchers
import kotlinx.coroutines.launch

@Composable
fun RetryFailedButton(enabled: Boolean) {
    val state = LocalLaunchyState
    val packState = LocalGameInstanceState
    OutlinedRedButton(
        enabled = enabled,
        onClick = {
            AppDispatchers.profileLaunch.launch {
                packState.startInstall(state, ignoreCachedCheck = true)
            }
        },
    ) {
        Text("Retry ${packState.queued.failures.size} failed downloads")
    }
}

@Composable
fun InstallButton(
    modifier: Modifier = Modifier,
    viewModel: InstanceViewModel = viewModel(),
) {
    val state by viewModel.installState.collectAsState()
    PrimaryButton(
        enabled = state == InstallState.Queued,
        onClick = { viewModel.installMods() },
        modifier = modifier.width(150.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Download, "Download")
            AnimatedVisibility(true, Modifier.animateContentSize()) {
                val text = when (state) {
                    InstallState.Queued, InstallState.Error -> "Install"
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
