package com.mineinabyss.launchy.instance.ui.components.buttons

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.mineinabyss.launchy.core.ui.components.OutlinedRedButton

@Composable
fun RetryFailedButton(
    failureCount: Int,
    onClick: () -> Unit,
) {
    OutlinedRedButton(
        onClick = onClick,
    ) {
        Text("Retry $failureCount failed downloads")
    }
}
