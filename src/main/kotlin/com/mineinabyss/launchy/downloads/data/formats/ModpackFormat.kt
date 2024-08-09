package com.mineinabyss.launchy.downloads.data.formats

import com.mineinabyss.launchy.instance.data.InstanceModel
import java.nio.file.Path

interface ModpackFormat {
    suspend fun prepareSource(instance: InstanceModel, download: Path)
    suspend fun loadPackFor(instance: InstanceModel): Result<Modpack>
}
