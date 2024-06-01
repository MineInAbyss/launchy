package com.mineinabyss.launchy.core.ui

import com.mineinabyss.launchy.auth.data.identity.IdentityDataSource
import com.mineinabyss.launchy.instance.data.storage.InstanceConfig

sealed interface Dialog {
    object None : Dialog

    object ChooseJVMPath : Dialog

    data class Auth(val verification: IdentityDataSource.VerificationRequired) : Dialog

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
        val info: InstanceConfig
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
