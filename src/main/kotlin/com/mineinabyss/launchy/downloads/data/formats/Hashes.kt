package com.mineinabyss.launchy.downloads.data.formats

import kotlinx.serialization.Serializable

@Serializable
data class Hashes(
    val sha1: String,
    val sha512: String,
)
