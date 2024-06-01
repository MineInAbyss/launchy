package com.mineinabyss.launchy.core.ui

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mineinabyss.launchy.config.data.Config
import com.mineinabyss.launchy.config.data.ConfigDataSource
import com.mineinabyss.launchy.instance.data.GameInstanceDataSource
import com.mineinabyss.launchy.util.AppDispatchers
import com.mineinabyss.launchy.util.Dirs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LaunchyViewModel(
    val configDataSource: ConfigDataSource
) : ViewModel() {
    private val _uiState = MutableStateFlow<LaunchyUiState>(LaunchyUiState.Loading)
    private val _config = MutableStateFlow(Config())

    val uiState = _uiState.asStateFlow()
    val config = _config.asStateFlow()

    init {
        viewModelScope.launch {
            setupFilesystem()
            tryLoadConfig()
            val instances = GameInstanceDataSource.readAll(Dirs.modpackConfigsDir)
            val config = config.value
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

    private suspend fun tryLoadConfig() = withContext(AppDispatchers.IO) {
        configDataSource.readConfig().onSuccess {
            _config.emit(it)
        }
    }
}
