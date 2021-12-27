package com.mineinabyss.launchy.data

import kotlinx.serialization.Serializable

@Serializable
data class Group(
    val name: String,
    val enabledByDefault: Boolean = false,
    val forced: Boolean = false,
)
