package com.mineinabyss.launchy.data.auth

import com.mineinabyss.launchy.data.Dirs
import jmccc.microsoft.MicrosoftAuthenticator
import jmccc.microsoft.entity.MicrosoftSession
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import kotlin.io.path.*

@Serializable
data class SessionStorage(
    var microsoftAccessToken: String?,
    var microsoftRefreshToken: String?,
    var minecraftAccessToken: String?,
    var xboxUserId: String?,
) {
    companion object {
        fun from(session: MicrosoftSession) = SessionStorage(
            session.microsoftAccessToken,
            session.microsoftRefreshToken,
            session.minecraftAccessToken,
            session.xboxUserId,
        )

        fun load(uuid: String): MicrosoftSession? {
            val targetFile = (Dirs.accounts / "$uuid.json")
            if (!targetFile.exists()) return null
            val session = runCatching { Json.decodeFromString(serializer(), targetFile.readText()) }
                .getOrNull() ?: return null

            return MicrosoftSession().apply {
                microsoftAccessToken = session.microsoftAccessToken
                microsoftRefreshToken = session.microsoftRefreshToken
                minecraftAccessToken = session.minecraftAccessToken
                xboxUserId = session.xboxUserId
            }
        }

        fun save(session: MicrosoftAuthenticator) {
            val auth = session.auth()
            val targetFile = (Dirs.accounts / "${auth.uuid}.json").createParentDirectories()
            targetFile.deleteIfExists()
            targetFile.createFile()
            Json.encodeToStream(
                serializer(),
                from(session.session),
                targetFile.outputStream()
            )
        }
    }
}
