package com.mineinabyss.launchy.ui.screens.addmodpack

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import com.mineinabyss.launchy.ui.elements.LaunchyDialog
import com.mineinabyss.launchy.ui.screens.Dialog
import com.mineinabyss.launchy.ui.screens.dialog

@Composable
fun AddModpackDialog() {
    LaunchyDialog(
        title = { Text("Add modpack", style = LocalTextStyle.current) },
        onAccept = { dialog = Dialog.None },
        onDecline = { dialog = Dialog.None },
        onDismiss = { dialog = Dialog.None },
        acceptText = "Add",
        declineText = "Cancel",
    ) {
        var text by remember { mutableStateOf("") }
        Column {
            Text("Add new modpack", style = LocalTextStyle.current)
            TextField(
                value = text,
                singleLine = true,
                onValueChange = { text = it },
                label = { Text("Modpack url") },
            )
        }
    }
}
