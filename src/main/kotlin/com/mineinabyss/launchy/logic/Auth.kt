package com.mineinabyss.launchy.logic

import com.mineinabyss.launchy.data.auth.SessionStorage
import com.mineinabyss.launchy.state.LaunchyState
import jmccc.microsoft.MicrosoftAuthenticator
import jmccc.microsoft.entity.MicrosoftVerification


object Auth {
    fun authFlow(
        state: LaunchyState,
        onGetMSA: (MicrosoftVerification) -> Unit,
        onGetSession: (MicrosoftAuthenticator) -> Unit,
    ) {
        val previousSession = state.currentProfileUUID?.let { SessionStorage.load(it) }
        if (previousSession != null) onGetSession(MicrosoftAuthenticator.session(previousSession) {
            onGetMSA(it)
        }) else onGetSession(MicrosoftAuthenticator.login {
            onGetMSA(it)
        })
    }

    fun hasPreviousSession(state: LaunchyState): Boolean {
        return state.currentProfileUUID?.let { SessionStorage.load(it) } != null
    }
}
