package com.mineinabyss.launchy.state.modpack

import androidx.compose.runtime.*
import com.mineinabyss.launchy.data.ModInfo
import com.mineinabyss.launchy.data.modpacks.Mod
import com.mineinabyss.launchy.logic.Progress
import com.mineinabyss.launchy.state.mutableStateSetOf

class DownloadState(val queue: DownloadQueueState) {
    val inProgressMods = mutableStateMapOf<Mod, Progress>()
    val inProgressConfigs = mutableStateMapOf<Mod, Progress>()
    val failed = mutableStateSetOf<Mod>()

    val isDownloading by derivedStateOf { inProgressMods.isNotEmpty() || inProgressConfigs.isNotEmpty() || installingProfile }

    // Caclculate the speed of the download
    val downloadSpeed by derivedStateOf {
        val total = inProgressMods.values.sumOf { it.bytesDownloaded }
        val time = inProgressMods.values.sumOf { it.timeElapsed }
        if (time == 0L) 0 else total / time
    }

    var installingProfile by mutableStateOf(false)
}
