package com.mineinabyss.launchy.downloads.data.sources

import com.mineinabyss.launchy.instance.data.InstanceModel

class LocalModpackDataSource : ModpackDataSource {
    override suspend fun skip(instance: InstanceModel) = true

    override suspend fun fetchLatestModsFor(instance: InstanceModel) = null
}
