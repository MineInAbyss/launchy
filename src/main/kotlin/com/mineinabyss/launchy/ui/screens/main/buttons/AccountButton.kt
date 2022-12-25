package com.mineinabyss.launchy.ui.screens.main.buttons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ManageAccounts
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.mineinabyss.launchy.ui.screens.Screen
import com.mineinabyss.launchy.ui.screens.screen

@Composable
fun AccountButton() {
    Button(onClick = { screen = Screen.Account }) {
        Icon(Icons.Rounded.ManageAccounts, contentDescription = "Account")
        Text("Account")
    }
}
