package com.mineinabyss.launchy.data.modpacks

import kotlinx.serialization.Serializable

@Serializable
class ModReference(
    val urlContains: String,
    val info: ModConfig? = null,
)
@Serializable
class ExtraPackInfo(
    val groups: List<Group> = listOf(),
    val modGroups: Map<String, Set<ModReference>> = mapOf(),
)
