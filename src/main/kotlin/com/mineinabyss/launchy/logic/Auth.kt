package com.mineinabyss.launchy.logic

import com.mineinabyss.launchy.data.auth.SessionStorage
import com.mineinabyss.launchy.data.config.PlayerProfile
import com.mineinabyss.launchy.state.ProfileState
import com.mineinabyss.launchy.ui.screens.Dialog
import com.mineinabyss.launchy.ui.screens.dialog
import jmccc.microsoft.MicrosoftAuthenticator
import jmccc.microsoft.entity.MicrosoftVerification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


object Auth {
    suspend fun authOrShowDialog(
        state: ProfileState,
        onAuthenticate: (MicrosoftAuthenticator) -> Unit = {},
    ) = coroutineScope {
        if (state.currentProfile == null) dialog = Dialog.Auth
        authFlow(
            state,
            onVerificationRequired = {
                Browser.browse(it.verificationUri)
                state.authCode = it.userCode
                dialog = Dialog.Auth
                println(state.authCode)
            },
            onAuthenticate = {
                launch(Dispatchers.IO) {
                    SessionStorage.save(it)
                }
                state.currentSession = it
                val auth = it.auth()
                state.currentProfile = PlayerProfile(auth.username, auth.uuid)
                dialog = Dialog.None
                onAuthenticate(it)
            }
        )
    }

    private fun authFlow(
        state: ProfileState,
        onVerificationRequired: (MicrosoftVerification) -> Unit,
        onAuthenticate: (MicrosoftAuthenticator) -> Unit,
    ) {
        val previousSession = state.currentProfile?.let { SessionStorage.load(it.uuid) }
        if (previousSession != null) onAuthenticate(MicrosoftAuthenticator.session(previousSession) {
            onVerificationRequired(it)
        }) else onAuthenticate(MicrosoftAuthenticator.login {
            onVerificationRequired(it)
        })
    }
}
