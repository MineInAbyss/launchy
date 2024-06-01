package com.mineinabyss.launchy.launcher.data

import com.mineinabyss.launchy.instance.data.GameInstanceDataSource

class ProcessRepository {
    private val launchedProcesses = mutableMapOf<String, Process>()

    fun processFor(instance: GameInstanceDataSource): Process? = launchedProcesses[instance.minecraftDir.toString()]
    fun setProcessFor(instance: GameInstanceDataSource, process: Process?) {
        if (process == null) launchedProcesses.remove(instance.minecraftDir.toString())
        else launchedProcesses[instance.minecraftDir.toString()] = process
    }
}
