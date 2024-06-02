package com.mineinabyss.launchy.downloads.data.formats

import com.mineinabyss.launchy.instance.data.ModListModel
import com.mineinabyss.launchy.instance.data.ModLoaderModel
import java.nio.file.Path

data class Modpack(
    val modLoader: ModLoaderModel,
    val modList: ModListModel,
    val configSources: List<Path> = listOf(),
)
