package com.mineinabyss.launchy.auth.data

import com.mineinabyss.launchy.util.serializers.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class ProfileModel(
    val name: String,
    val uuid: @Serializable(with = UUIDSerializer::class) UUID,
)
