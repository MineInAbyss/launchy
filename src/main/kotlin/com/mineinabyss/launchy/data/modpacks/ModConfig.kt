package com.mineinabyss.launchy.data.modpacks

import com.mineinabyss.launchy.data.modpacks.formats.ModDownloadPath
import kotlinx.serialization.Serializable

@Serializable
data class ModConfig(
    val id: String? = null,
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
    val downloadPath: ModDownloadPath? = null,
    val requires: List<String> = emptyList(),
)
