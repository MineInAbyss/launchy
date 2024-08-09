package com.mineinabyss.launchy.core.ui

sealed interface LaunchyUiState {
    object Loading : LaunchyUiState
    data class Ready(
        val ui: UiState,
    ) : LaunchyUiState
}
