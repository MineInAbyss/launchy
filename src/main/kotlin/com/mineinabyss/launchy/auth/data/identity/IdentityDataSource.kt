package com.mineinabyss.launchy.auth.data.identity

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.mineinabyss.launchy.auth.data.ProfileModel
import com.mineinabyss.launchy.core.data.TasksRepository
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import net.raphimc.minecraftauth.MinecraftAuth
import net.raphimc.minecraftauth.step.java.session.StepFullJavaSession.FullJavaSession
import net.raphimc.minecraftauth.step.msa.StepMsaDeviceCode.MsaDeviceCode
import net.raphimc.minecraftauth.step.msa.StepMsaDeviceCode.MsaDeviceCodeCallback
import java.util.*


class IdentityDataSource(
    val settings: Settings,
    val tasks: TasksRepository,
) {
    private val httpClient = MinecraftAuth.createHttpClient()
    private val deviceCodeLogin = MinecraftAuth.ALT_JAVA_DEVICE_CODE_LOGIN
    //TODO override with our own oauth app
//        .builder()
//        .withClientId("00000000402b5328")
//        .withScope("service::user.auth.xboxlive.com::MBI_SSL")
//        .deviceCode()
//        .withDeviceToken("Win32")
//        .sisuTitleAuthentication("rp://api.minecraftservices.com/")
//        .buildMinecraftJavaProfileStep(true)

    val gson = GsonBuilder().setPrettyPrinting().create()

    fun load(uuid: UUID): Result<FullJavaSession?> {
        val saved = settings.getStringOrNull("session-$uuid") ?: return Result.success(null)
        return runCatching {
            deviceCodeLogin.fromJson(JsonParser.parseString(saved).asJsonObject)
        }
    }

    fun forgetSession(uuid: UUID) {
        settings.remove("session-$uuid")
    }

    fun save(session: FullJavaSession) {
        val json = deviceCodeLogin.toJson(session)
        settings["session-${session.mcProfile.id}"] = gson.toJson(json)
    }

    class VerificationRequired(
        val code: String,
        val redirectTo: String,
    )

    fun authFlow(
        profile: ProfileModel?,
        onVerificationRequired: (VerificationRequired) -> Unit,
    ): FullJavaSession {
        // Attempt existing session refresh
        val previousSession = profile?.uuid?.let { load(it) }?.getOrNull()
        if (previousSession != null) {
            println("Refreshing token")
            runCatching { deviceCodeLogin.refresh(httpClient, previousSession) }
                .onSuccess {
                    return it
                }
        }
        // Prompt user to log in with device code
        val javaSession = deviceCodeLogin.getFromInput(
            httpClient,
            MsaDeviceCodeCallback { msaDeviceCode: MsaDeviceCode ->
                onVerificationRequired(
                    VerificationRequired(
                        msaDeviceCode.userCode,
                        msaDeviceCode.directVerificationUri
                    )
                )
            })
        save(javaSession)
        return javaSession
    }
}
