package com.mineinabyss.launchy.ui.elements

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


val PrimaryButtonColors @Composable get() = ButtonDefaults.buttonColors(
    containerColor = MaterialTheme.colorScheme.primaryContainer,
    contentColor = MaterialTheme.colorScheme.primary
)
val SecondaryButtonColors @Composable get() = ButtonDefaults.buttonColors(
    containerColor = MaterialTheme.colorScheme.secondaryContainer,
    contentColor = MaterialTheme.colorScheme.secondary
)

val PrimaryIconButtonColors @Composable get() = IconButtonDefaults.iconButtonColors(
    containerColor = MaterialTheme.colorScheme.primaryContainer,
    contentColor = MaterialTheme.colorScheme.primary
)
val SecondaryIconButtonColors @Composable get() = IconButtonDefaults.iconButtonColors(
    containerColor = MaterialTheme.colorScheme.secondaryContainer,
    contentColor = MaterialTheme.colorScheme.secondary
)

@Composable
fun PrimaryButton(
    enabled: Boolean = true,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Button(
        enabled = enabled,
        onClick = onClick,
        colors = PrimaryButtonColors,
        modifier = modifier,
    ) { content() }
}

@Composable
fun SecondaryButton(
    enabled: Boolean = true,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {

    Button(
        enabled = enabled,
        onClick = onClick,
        colors = SecondaryButtonColors,
        modifier = modifier,
    ) { content() }
}

@Composable
fun OutlinedRedButton(
    enabled: Boolean = true,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    OutlinedButton(
        enabled = enabled,
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
        modifier = modifier,
    ) {
        content()
    }
}
