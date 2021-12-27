package com.mineinabyss.launchy.data

import kotlinx.serialization.Serializable

@Serializable
data class Mod(
    val name: String,
    val desc: String,
    val url: String,
)
