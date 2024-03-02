package com.mineinabyss.launchy.logic

import com.mineinabyss.launchy.data.auth.SessionStorage
import com.mineinabyss.launchy.state.ProfileState
import com.mineinabyss.launchy.ui.screens.Dialog
import com.mineinabyss.launchy.ui.screens.dialog
import jmccc.microsoft.MicrosoftAuthenticator
import jmccc.microsoft.entity.MicrosoftVerification


object Auth {
    fun authOrShowDialog(state: ProfileState, onComlete: () -> Unit) {
        authFlow(state,
            onVerificationRequired = { dialog = Dialog.Auth },
            onAuthenticate = { onComlete() })
    }

    fun authFlow(
        state: ProfileState,
        onVerificationRequired: (MicrosoftVerification) -> Unit,
        onAuthenticate: (MicrosoftAuthenticator) -> Unit,
    ) {
        val previousSession = state.currentProfileUUID?.let { SessionStorage.load(it) }
        if (previousSession != null) onAuthenticate(MicrosoftAuthenticator.session(previousSession) {
            onVerificationRequired(it)
        }) else onAuthenticate(MicrosoftAuthenticator.login {
            onVerificationRequired(it)
        })
    }
}
