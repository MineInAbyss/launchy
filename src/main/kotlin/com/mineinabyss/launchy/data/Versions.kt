package com.mineinabyss.launchy.data

import com.charleskorn.kaml.Yaml
import com.mineinabyss.launchy.logic.Downloader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlin.io.path.inputStream

@Serializable
data class Versions(
    val modGroups: Map<GroupName, Set<Mod>>
) {
    val mods: Map<ModName, Mod> = modGroups.values
        .flatten()
        .associateBy { it.name }

    companion object {
        const val VERSIONS_URL = "https://raw.githubusercontent.com/MineInAbyss/server-config/master/versions.yml"

        suspend fun readLatest(): Versions = withContext(Dispatchers.IO) {
            Downloader.download(VERSIONS_URL, Dirs.versionsFile)
            Yaml.default.decodeFromStream(serializer(), Dirs.versionsFile.inputStream())
        }
    }
}
