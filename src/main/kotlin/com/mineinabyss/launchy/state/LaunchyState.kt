package com.mineinabyss.launchy.state

import androidx.compose.runtime.*
import com.mineinabyss.launchy.data.config.Config
import com.mineinabyss.launchy.data.config.GameInstance
import com.mineinabyss.launchy.state.modpack.ModpackState
import java.util.*

class LaunchyState(
    // Config should never be mutated unless it also updates UI state
    private val config: Config,
    private val instances: List<GameInstance>
) {
    val profile = ProfileState(config)
    var modpackState: ModpackState? by mutableStateOf(null)
    private val launchedProcesses = mutableStateMapOf<String, Process>()
    val jvm = JvmState(config)
    var preferHue: Float by mutableStateOf(config.preferHue ?: 0f)

    val gameInstances = mutableStateListOf<GameInstance>().apply {
        addAll(instances)
    }

    val inProgressTasks = mutableStateMapOf<String, InProgressTask>()

    fun processFor(instance: GameInstance): Process? = launchedProcesses[instance.minecraftDir.toString()]
    fun setProcessFor(instance: GameInstance, process: Process?) {
        if (process == null) launchedProcesses.remove(instance.minecraftDir.toString())
        else launchedProcesses[instance.minecraftDir.toString()] = process
    }

    // If any state is true, we consider import handled and move on
    var handledImportOptions by mutableStateOf(
        config.handledImportOptions
    )

    var onboardingComplete by mutableStateOf(config.onboardingComplete)

    fun saveToConfig() {
        config.copy(
            handledImportOptions = handledImportOptions,
            onboardingComplete = onboardingComplete,
            currentProfile = profile.currentProfile,
            javaPath = jvm.javaPath?.toString(),
            jvmArguments = jvm.userJvmArgs,
            memoryAllocation = jvm.userMemoryAllocation,
            useRecommendedJvmArguments = jvm.useRecommendedJvmArgs,
            preferHue = preferHue,
        ).save()
    }

    inline fun <T> runTask(key: String, task: InProgressTask, run: () -> T): T {
        try {
            inProgressTasks[key] = task
            return run()
        } finally {
            inProgressTasks.remove(key)
        }
    }
}

fun <T> mutableStateSetOf() = Collections.newSetFromMap(mutableStateMapOf<T, Boolean>())
