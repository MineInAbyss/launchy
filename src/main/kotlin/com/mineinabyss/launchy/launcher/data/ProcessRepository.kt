package com.mineinabyss.launchy.launcher.data

import com.mineinabyss.launchy.util.InstanceKey

class ProcessRepository {
    private val launchedProcesses = mutableMapOf<InstanceKey, Process>()

    fun processFor(instance: InstanceKey): Process? = launchedProcesses[instance]

    fun setProcessFor(instance: InstanceKey, process: Process?) {
        if (process == null) launchedProcesses.remove(instance)
        else launchedProcesses[instance] = process
    }
}
