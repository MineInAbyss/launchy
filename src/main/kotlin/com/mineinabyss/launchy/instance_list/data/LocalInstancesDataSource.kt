package com.mineinabyss.launchy.instance_list.data

import com.mineinabyss.launchy.instance.data.GameInstanceDataSource
import java.nio.file.Path
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries

class LocalInstancesDataSource(
    val rootDir: Path,
) {
    fun getInstanceList(): List<GameInstanceDataSource> = rootDir
        .listDirectoryEntries()
        .filter { it.isDirectory() }
        .mapNotNull {
            runCatching { GameInstanceDataSource(it) }
                .onFailure { it.printStackTrace() }
                .getOrNull()
        }
}
