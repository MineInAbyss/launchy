package com.mineinabyss.launchy.auth.data.identity

import kotlinx.serialization.Serializable

@Serializable
data class SessionStorage(
    val microsoftAccessToken: String,
    val microsoftRefreshToken: String,
    val minecraftAccessToken: String,
    val xboxUserId: String,
)
