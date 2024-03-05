package com.mineinabyss.launchy.ui.screens

import com.mineinabyss.launchy.data.modpacks.ModpackInfo

sealed interface Dialog {
    object None : Dialog
    object Auth : Dialog

    class Options(
        val title: String,
        val message: String,
        val acceptText: String,
        val declineText: String,
        val onAccept: () -> Unit,
        val onDecline: () -> Unit,
    ) : Dialog

    class Error(val title: String, val message: String) : Dialog

    class ConfirmImportModpackDialog(
        val info: ModpackInfo
    )
}
