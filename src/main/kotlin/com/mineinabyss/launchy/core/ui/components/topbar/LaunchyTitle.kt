package com.mineinabyss.launchy.core.ui.components.topbar

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.mineinabyss.launchy.core.ui.Constants

@Composable
fun LaunchyTitle() {
    Row {
        Icon(
            Icons.Rounded.RocketLaunch,
            contentDescription = "Launchy",
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            "Launchy - ${Constants.APP_VERSION ?: "dev"}",
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
