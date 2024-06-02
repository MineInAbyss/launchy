package com.mineinabyss.launchy.core.ui

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mineinabyss.launchy.config.data.ConfigRepository
import com.mineinabyss.launchy.util.Dirs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LaunchyViewModel(
    val configRepo: ConfigRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<LaunchyUiState>(LaunchyUiState.Loading)

    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            setupFilesystem()
            configRepo.tryLoadConfig()
            _uiState.emit(
                LaunchyUiState.Ready(
                    UiState(config),
                    instances,
                )
            )
        }
    }

    val lastPlayed = mutableStateMapOf<String, Long>().apply {
        putAll(config.lastPlayedMap)
    }

    // If any state is true, we consider import handled and move on
    var handledImportOptions by mutableStateOf(
        config.handledImportOptions
    )

    var onboardingComplete by mutableStateOf(config.onboardingComplete)


    fun saveToConfig() {
        config.value.copy(
            handledImportOptions = handledImportOptions,
            onboardingComplete = onboardingComplete,
            currentProfile = profile.currentProfile,
            javaPath = jvm.javaPath?.toString(),
            jvmArguments = jvm.userJvmArgs,
            memoryAllocation = jvm.userMemoryAllocation,
            useRecommendedJvmArguments = jvm.useRecommendedJvmArgs,
            preferHue = ui.preferHue,
            startInFullscreen = ui.fullscreen,
            lastPlayedMap = lastPlayed
        ).save()
    }

    private fun setupFilesystem() {
        Dirs.createDirs()
        Dirs.createConfigFiles()
    }
}
