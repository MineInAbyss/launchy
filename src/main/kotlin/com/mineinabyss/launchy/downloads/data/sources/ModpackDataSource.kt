package com.mineinabyss.launchy.downloads.data.sources

import com.mineinabyss.launchy.instance.data.InstanceModel
import java.nio.file.Path

interface ModpackDataSource {
    suspend fun skip(instance: InstanceModel): Boolean = false

    suspend fun fetchLatestModsFor(instance: InstanceModel): Path?
}
