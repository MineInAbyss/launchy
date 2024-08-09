package com.mineinabyss.launchy.instance.data.storage

import com.mineinabyss.launchy.downloads.data.formats.ModDownloadPath
import com.mineinabyss.launchy.util.ModID
import kotlinx.serialization.Serializable

@Serializable
data class ModConfig(
    val id: ModID? = null,
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
