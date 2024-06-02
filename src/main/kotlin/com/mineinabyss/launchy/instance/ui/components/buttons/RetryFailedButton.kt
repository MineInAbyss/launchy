package com.mineinabyss.launchy.instance.ui.components.buttons

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.mineinabyss.launchy.core.ui.components.OutlinedRedButton
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
