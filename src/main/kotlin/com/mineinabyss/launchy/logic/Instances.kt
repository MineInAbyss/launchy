package com.mineinabyss.launchy.logic

import com.mineinabyss.launchy.data.Dirs
import com.mineinabyss.launchy.data.config.GameInstance
import com.mineinabyss.launchy.data.config.GameInstanceConfig
import com.mineinabyss.launchy.state.InProgressTask
import com.mineinabyss.launchy.state.LaunchyState
import com.mineinabyss.launchy.ui.screens.Screen
import com.mineinabyss.launchy.ui.screens.screen
import kotlinx.coroutines.launch
import kotlin.io.path.*

object Instances {
    @OptIn(ExperimentalPathApi::class)
    fun GameInstance.delete(state: LaunchyState, deleteDotMinecraft: Boolean) {
        try {
            state.inProgressTasks["deleteInstance"] = InProgressTask("Deleting instance ${config.name}")
            state.gameInstances.remove(this)
            state.ioScope.launch {
                if (deleteDotMinecraft) minecraftDir.deleteRecursively()
                configDir.deleteRecursively()
            }
        } finally {
            state.inProgressTasks.remove("deleteInstance")
        }
    }

    fun GameInstance.updateInstance(
        state: LaunchyState,
        onSuccess: () -> Unit = {},
    ) {
        state.inProgressTasks["updateInstance"] = InProgressTask("Updating instance: ${config.name}")
        try {
            screen = Screen.Default
            enabled = false
            val index = state.gameInstances.indexOf(this)
            state.ioScope.launch {
                val cloudUrl = config.cloudInstanceURL
                if (cloudUrl != null) {
                    val newCloudInstance = Dirs.tmpCloudInstance(cloudUrl)
                    Downloader.download(cloudUrl, newCloudInstance, whenChanged = {
                        instanceFile.copyTo(configDir / "instance-backup.yml", overwrite = true)
                        GameInstanceConfig.read(newCloudInstance).onSuccess { cloudConfig ->
                            instanceFile.deleteIfExists()
                            instanceFile.createFile()
                            config.copy(
                                description = cloudConfig.description,
                                backgroundURL = cloudConfig.backgroundURL,
                                logoURL = cloudConfig.logoURL,
                                hue = cloudConfig.hue,
                                source = cloudConfig.source,
                            ).saveTo(instanceFile)
                        }
                    })
                }

                // Handle case where we just updated from cloud
                val config = GameInstanceConfig.read(instanceFile).getOrElse { config }

                config.source.updateInstance(this@updateInstance)
                    .showDialogOnError("Failed to update instance ${config.name}")
                    .onFailure { it.printStackTrace() }
                    .onSuccess {
                        state.gameInstances[index] = it
                        onSuccess()
                    }
            }
        } finally {
            state.inProgressTasks.remove("updateInstance")
        }
    }
}
