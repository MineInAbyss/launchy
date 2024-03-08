package com.mineinabyss.launchy.data.modpacks

import kotlinx.serialization.Serializable

@Serializable
data class ModInfo(
    val name: String,
    val license: String = "",
    val homepage: String = "",
    val desc: String = "",
    val url: String = "",
    val configUrl: String = "",
    val configDesc: String = "",
    val forceConfigDownload: Boolean = false,
    val dependency: Boolean = false,
    val incompatibleWith: List<String> = emptyList(),
    val downloadPath: String? = null,
    val requires: List<String> = emptyList(),
)
