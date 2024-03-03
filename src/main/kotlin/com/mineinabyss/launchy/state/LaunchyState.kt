package com.mineinabyss.launchy.state

import androidx.compose.runtime.*
import com.mineinabyss.launchy.data.Dirs
import com.mineinabyss.launchy.data.config.Config
import com.mineinabyss.launchy.data.modpacks.ModpackInfo
import com.mineinabyss.launchy.state.modpack.ModpackState
import java.util.*
import kotlin.io.path.div
import kotlin.io.path.exists

class LaunchyState(
    // Config should never be mutated unless it also updates UI state
    private val config: Config,
) {
    val profile = ProfileState(config)
    var modpackState: ModpackState? by mutableStateOf(null)
    var launchedProcesses = mutableStateMapOf<String, Process>()

    fun processFor(pack: ModpackInfo): Process? = launchedProcesses[pack.folderName]

    // If any state is true, we consider import handled and move on
    var handledImportOptions by mutableStateOf(
        config.handledImportOptions ||
                (Dirs.mineinabyss / "options.txt").exists() ||
                !Dirs.minecraft.exists()
    )

    var onboardingComplete by mutableStateOf(config.onboardingComplete)

    val downloadedModpacks = mutableStateListOf<ModpackInfo>().apply {
        addAll(config.modpacks)
    }

    fun saveToConfig() {
        config.copy(
            handledImportOptions = handledImportOptions,
            onboardingComplete = onboardingComplete,
            currentProfile = profile.currentProfile,
            modpacks = downloadedModpacks,
        ).save()
    }
}

fun <T> mutableStateSetOf() = Collections.newSetFromMap(mutableStateMapOf<T, Boolean>())
