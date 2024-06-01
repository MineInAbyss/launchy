package com.mineinabyss.launchy.instance_list.data

import com.mineinabyss.launchy.config.data.GameInstance
import com.mineinabyss.launchy.config.data.GameInstanceConfig
import com.mineinabyss.launchy.core.data.Downloader
import com.mineinabyss.launchy.core.ui.LaunchyState
import com.mineinabyss.launchy.core.ui.Screen
import com.mineinabyss.launchy.core.ui.screen
import com.mineinabyss.launchy.util.AppDispatchers
import com.mineinabyss.launchy.util.Dirs
import com.mineinabyss.launchy.util.InProgressTask
import com.mineinabyss.launchy.util.showDialogOnError
import kotlinx.coroutines.launch
import kotlin.io.path.*

object Instances {
    @OptIn(ExperimentalPathApi::class)
    fun GameInstance.delete(state: LaunchyState, deleteDotMinecraft: Boolean) {
        state.gameInstances.remove(this)
        state.runTask("deleteInstance", InProgressTask("Deleting instance ${config.name}")) {
            AppDispatchers.IO.launch {
                if (deleteDotMinecraft) minecraftDir.deleteRecursively()
                configDir.deleteRecursively()
            }
        }
    }

    fun GameInstance.updateInstance(
        state: LaunchyState,
        onSuccess: () -> Unit = {},
    ) {
        screen = Screen.Default
        enabled = false
        val index = state.gameInstances.indexOf(this)
        AppDispatchers.IO.launch {
            state.runTask("updateInstance", InProgressTask("Updating instance: ${config.name}")) {
                val cloudUrl = config.cloudInstanceURL
                if (cloudUrl != null) {
                    val newCloudInstancePath = Dirs.createTempCloudInstanceFile()
                    Downloader.download(
                        cloudUrl, newCloudInstancePath, Downloader.Options(
                            saveModifyHeadersFor = this@updateInstance
                        )
                    )
                    instanceFile.copyTo(configDir / "instance-backup.yml", overwrite = true)
                    GameInstanceConfig.read(newCloudInstancePath).onSuccess { cloudConfig ->
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
        }
    }
}
