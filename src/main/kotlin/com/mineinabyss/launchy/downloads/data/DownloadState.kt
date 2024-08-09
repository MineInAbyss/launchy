package com.mineinabyss.launchy.downloads.data

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import com.mineinabyss.launchy.downloads.data.formats.Mod
import com.mineinabyss.launchy.util.Progress

class DownloadState {
    val inProgressMods = mutableStateMapOf<Mod, Progress>()
    val inProgressConfigs = mutableStateMapOf<Mod, Progress>()

    val isDownloading by derivedStateOf { inProgressMods.isNotEmpty() || inProgressConfigs.isNotEmpty() }

    // Caclculate the speed of the download
    val downloadSpeed by derivedStateOf {
        val total = inProgressMods.values.sumOf { it.bytesDownloaded }
        val time = inProgressMods.values.sumOf { it.timeElapsed }
        if (time == 0L) 0 else total / time
    }

}
