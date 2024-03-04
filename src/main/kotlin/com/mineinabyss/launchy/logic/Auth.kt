package com.mineinabyss.launchy.logic

import com.mineinabyss.launchy.data.auth.SessionStorage
import com.mineinabyss.launchy.data.config.PlayerProfile
import com.mineinabyss.launchy.state.ProfileState
import com.mineinabyss.launchy.ui.screens.Dialog
import com.mineinabyss.launchy.ui.screens.dialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import net.raphimc.minecraftauth.MinecraftAuth
import net.raphimc.minecraftauth.step.java.session.StepFullJavaSession.FullJavaSession
import net.raphimc.minecraftauth.step.msa.StepMsaDeviceCode.MsaDeviceCode
import net.raphimc.minecraftauth.step.msa.StepMsaDeviceCode.MsaDeviceCodeCallback


object Auth {
    suspend fun authOrShowDialog(
        state: ProfileState,
        onAuthenticate: (FullJavaSession) -> Unit = {},
    ) = coroutineScope {
        launch(Dispatchers.IO) {
            if (state.currentProfile == null) dialog = Dialog.Auth
            authFlow(
                state,
                onVerificationRequired = {
                    Browser.browse(it.redirectTo)
                    state.authCode = it.code
                    dialog = Dialog.Auth
                    println(state.authCode)
                },
                onAuthenticate = {
                    launch(Dispatchers.IO) {
                        SessionStorage.save(it)
                    }
                    state.currentSession = it
                    state.currentProfile = PlayerProfile(it.mcProfile.name, it.mcProfile.id)
                    dialog = Dialog.None
                    onAuthenticate(it)
                }
            )
        }
    }

    class VerificationRequired(
        val code: String,
        val redirectTo: String,
    )

    private fun authFlow(
        state: ProfileState,
        onVerificationRequired: (VerificationRequired) -> Unit,
        onAuthenticate: (FullJavaSession) -> Unit,
    ) {
        val httpClient = MinecraftAuth.createHttpClient()
        val previousSession = state.currentProfile?.let { SessionStorage.load(it.uuid) }
        if(previousSession != null) {
            val refreshedSession = MinecraftAuth.JAVA_DEVICE_CODE_LOGIN.refresh(httpClient, previousSession)
            onAuthenticate(refreshedSession)
            return
        }

        val javaSession = MinecraftAuth.JAVA_DEVICE_CODE_LOGIN.getFromInput(
            httpClient,
            MsaDeviceCodeCallback { msaDeviceCode: MsaDeviceCode ->
                onVerificationRequired(
                    VerificationRequired(
                        msaDeviceCode.userCode,
                        msaDeviceCode.directVerificationUri
                    )
                )
            })
        onAuthenticate(javaSession)
    }
}
