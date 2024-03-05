package com.mineinabyss.launchy.data.auth

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.mineinabyss.launchy.data.Dirs
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import net.raphimc.minecraftauth.MinecraftAuth
import net.raphimc.minecraftauth.step.java.session.StepFullJavaSession.FullJavaSession
import java.io.InputStreamReader
import java.util.*
import kotlin.io.path.*

@Serializable
data class SessionStorage(
    val microsoftAccessToken: String,
    val microsoftRefreshToken: String,
    val minecraftAccessToken: String,
    val xboxUserId: String,
) {
    companion object {
        val gson = GsonBuilder().setPrettyPrinting().create()

        fun load(uuid: UUID): FullJavaSession? {
            val targetFile = (Dirs.accounts / "$uuid.json")
            if (!targetFile.exists()) return null
            return runCatching {
                MinecraftAuth.JAVA_DEVICE_CODE_LOGIN.fromJson(
                    JsonParser.parseString(
                        targetFile.readText()
                    ).asJsonObject
                )
            }.onFailure {
                println("Failed to load session for $uuid, ignoring file")
                it.printStackTrace()
            }.getOrNull()
        }

        fun save(session: FullJavaSession) {
            val json = MinecraftAuth.JAVA_DEVICE_CODE_LOGIN.toJson(session)
            val targetFile = (Dirs.accounts / "${session.mcProfile.id}.json").createParentDirectories()
            targetFile.deleteIfExists()
            targetFile.createFile()
            targetFile.writeText(gson.toJson(json))

        }
    }
}
