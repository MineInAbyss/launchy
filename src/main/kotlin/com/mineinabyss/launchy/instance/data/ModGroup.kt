package com.mineinabyss.launchy.instance.data

import kotlinx.serialization.Serializable

@Serializable
data class ModGroup(
    val name: String,
    val enabledByDefault: Boolean = false,
    val forceEnabled: Boolean = false,
    val forceDisabled: Boolean = false,
)
