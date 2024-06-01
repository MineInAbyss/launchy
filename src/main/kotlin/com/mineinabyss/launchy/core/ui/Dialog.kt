package com.mineinabyss.launchy.core.ui

import com.mineinabyss.launchy.instance.data.GameInstanceConfig

sealed interface Dialog {
    object None : Dialog

    object ChooseJVMPath : Dialog

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
        val info: GameInstanceConfig
    )

    companion object {
        fun fromException(exception: Throwable, title: String? = null): Error {
            return Error(
                title ?: "Error",
                exception
                    .stackTraceToString()
                    .split("\n").joinToString("\n", limit = 5)
            )
        }
    }
}
