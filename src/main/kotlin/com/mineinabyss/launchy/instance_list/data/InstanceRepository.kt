package com.mineinabyss.launchy.instance_list.data

import com.mineinabyss.launchy.core.data.FileSystemDataSource
import com.mineinabyss.launchy.core.data.TasksRepository
import com.mineinabyss.launchy.core.ui.screens.Screen
import com.mineinabyss.launchy.core.ui.screens.screen
import com.mineinabyss.launchy.instance.data.InstanceModel
import com.mineinabyss.launchy.util.AppDispatchers
import com.mineinabyss.launchy.util.InProgressTask
import com.mineinabyss.launchy.util.InstanceKey
import com.mineinabyss.launchy.util.showDialogOnError
import io.ktor.http.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

class InstanceRepository(
    val local: LocalInstancesDataSource,
    val remote: RemoteInstanceDataSource,
    val tasks: TasksRepository,
    val files: FileSystemDataSource
) {
    private val _instances = MutableStateFlow(mapOf<InstanceKey, InstanceModel>())

    val instances = _instances.asStateFlow()

    suspend fun delete(
        key: InstanceKey,
        deleteMinecraftDir: Boolean
    ) = withContext(AppDispatchers.IO) {
        val instance = _instances.value[key] ?: return@withContext
        tasks.run("deleteInstance", InProgressTask("Deleting instance ${instance.config.name}")) {
            local.deleteInstance(instance, deleteMinecraftDir)
            _instances.update { it.minus(key) }
        }
    }

    suspend fun fetchCloudInstanceUpdates(
        key: InstanceKey
    ) = withContext(AppDispatchers.IO) {
        val instance = _instances.value[key] ?: return@withContext
        screen = Screen.Default
//       TODO enabled = false
        tasks.run("updateInstance", InProgressTask("Updating instance: ${instance.config.name}")) {
            val cloudUrl = instance.config.cloudInstanceURL ?: return@withContext
            remote.fetchUpdatesForInstance(instance.config, Url(cloudUrl))
                .mapCatching { merged ->
                    val model = instance.copy(config = merged)
                    local.saveInstance(model)
                    model
                }
                .showDialogOnError("Failed to update instance ${instance.config.name}")
                .onSuccess { merged ->
                    _instances.update { it + (key to merged) }
                }
        }
    }

//    suspend fun saveOnChanges() {
//        instances.distinctUntilChanged { old, new ->  }.collectLatest { instances ->
//            instances
//            files.scheduleWrite()
//        }
//    }
}
