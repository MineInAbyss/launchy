package com.mineinabyss.launchy.core.data

import io.github.irgaly.kfswatch.KfsDirectoryWatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import java.nio.file.Path
import kotlin.coroutines.coroutineContext
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.pathString

class FileSystemDataSource {
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun watchDirectory(path: Path): Flow<List<Path>> {
        val watcher = KfsDirectoryWatcher(CoroutineScope(coroutineContext))
        watcher.add(path.pathString)
        return watcher.onEventFlow.mapLatest {
            path.listDirectoryEntries(glob = "*jar").toList()
        }
    }

    suspend fun scheduleWrite(path: Path, write: () -> Unit) {

    }
}
