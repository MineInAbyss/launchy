package com.mineinabyss.launchy.state

import androidx.compose.runtime.*
import com.mineinabyss.launchy.data.*
import com.mineinabyss.launchy.data.config.Config
import com.mineinabyss.launchy.state.modpack.SelectedModpackState
import java.util.*
import kotlin.io.path.div
import kotlin.io.path.exists

class LaunchyState(
    // Config should never be mutated unless it also updates UI state
    private val config: Config,
) {
    val profile = ProfileState(config)
    val modpackState: SelectedModpackState? by mutableStateOf(null)
    var currentLaunchProcess: Process? by mutableStateOf(null)


    // If any state is true, we consider import handled and move on
    var handledImportOptions by mutableStateOf(
        config.handledImportOptions ||
                (Dirs.mineinabyss / "options.txt").exists() ||
                !Dirs.minecraft.exists()
    )

    var onboardingComplete by mutableStateOf(config.onboardingComplete)


    fun saveToConfig() {
        config.copy(
            handledImportOptions = handledImportOptions,
            onboardingComplete = onboardingComplete,
            currentProfileUUID = profile.currentProfileUUID,
        ).save()
    }
}

fun <T> mutableStateSetOf() = Collections.newSetFromMap(mutableStateMapOf<T, Boolean>())
