package com.mineinabyss.launchy.instance.data.formats

import com.mineinabyss.launchy.instance.data.ModGroup
import com.mineinabyss.launchy.instance.data.storage.ModConfig
import kotlinx.serialization.Serializable

@Serializable
class ModReference(
    val urlContains: String,
    val info: ModConfig? = null,
)
@Serializable
class ExtraPackInfo(
    val groups: List<ModGroup> = listOf(),
    val modGroups: Map<String, Set<ModReference>> = mapOf(),
)
