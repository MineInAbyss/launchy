package com.mineinabyss.launchy.auth.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.auth.ui.ProfileViewModel
import com.mineinabyss.launchy.util.koinViewModel

@Composable
fun AccountsPopup(
    viewModel: ProfileViewModel = koinViewModel(),
    onLogout: () -> Unit
) {
    Surface(
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.size(48.dp)
    ) {
        val currentProfile by viewModel.profile.collectAsState()
        if (currentProfile != null) {
            IconButton(
                onClick = {
                    viewModel.logout()
                    onLogout()
                },
            ) {
                Icon(
                    Icons.AutoMirrored.Rounded.Logout,
                    "Logout",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
