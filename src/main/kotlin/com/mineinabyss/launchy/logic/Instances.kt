package com.mineinabyss.launchy.logic

import com.mineinabyss.launchy.data.config.GameInstance
import com.mineinabyss.launchy.state.InProgressTask
import com.mineinabyss.launchy.state.LaunchyState
import kotlinx.coroutines.launch
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.deleteRecursively

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
}
