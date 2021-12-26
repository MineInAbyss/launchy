package com.mineinabyss.launchy.data

import com.charleskorn.kaml.Yaml
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
        suspend fun readLatest(): Versions {
            return withContext(Dispatchers.IO) {
                delay(1000)
//                Downloader.download("", Dirs.versionsFile)
                Yaml.default.decodeFromStream(serializer(), Dirs.versionsFile.inputStream())
            }
        }
    }
}
