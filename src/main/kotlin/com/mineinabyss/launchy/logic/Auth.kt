package com.mineinabyss.launchy.logic

import com.mineinabyss.launchy.data.auth.SessionStorage
import com.mineinabyss.launchy.data.config.PlayerProfile
import com.mineinabyss.launchy.state.InProgressTask
import com.mineinabyss.launchy.state.LaunchyState
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
import java.util.*


object Auth {
    suspend fun authOrShowDialog(
        state: LaunchyState,
        profile: ProfileState,
        onAuthenticate: (FullJavaSession) -> Unit = {},
    ) = coroutineScope {
        launch(Dispatchers.IO) {
            if (profile.currentProfile == null) dialog = Dialog.Auth
            state.runTask("auth", InProgressTask("Authenticating")) {
                authFlow(
                    profile,
                    onVerificationRequired = {
                        state.inProgressTasks.remove("auth")
                        DesktopHelpers.browse(it.redirectTo)
                        profile.authCode = it.code
                        dialog = Dialog.Auth
                        println(profile.authCode)
                    },
                    onAuthenticate = {
                        launch(Dispatchers.IO) {
                            SessionStorage.save(it)
                        }
                        profile.currentSession = it
                        profile.currentProfile = PlayerProfile(it.mcProfile.name, it.mcProfile.id)
                        dialog = Dialog.None
                        onAuthenticate(it)
                    }
                )
            }
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
        val testAuth = MinecraftAuth.builder()
            .withClientId("00000000402b5328")
            .withScope("service::user.auth.xboxlive.com::MBI_SSL")
            .deviceCode()
            .withDeviceToken("Win32")
            .sisuTitleAuthentication("https://multiplayer.minecraft.net/")
            .buildMinecraftJavaProfileStep(true);
        val httpClient = MinecraftAuth.createHttpClient()
        val previousSession = state.currentProfile?.let { SessionStorage.load(it.uuid) }
        if (previousSession != null) {
            val refreshedSession = testAuth.refresh(httpClient, previousSession)
            onAuthenticate(refreshedSession)
            return
        }
        val javaSession = testAuth.getFromInput(
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

    fun ProfileState.logout(uuid: UUID) {
        SessionStorage.deleteIfExists(uuid)
        if (currentProfile?.uuid == uuid) {
            currentProfile = null
            currentSession = null
        }
    }
}
