package com.mineinabyss.launchy.instance.ui

sealed interface ModListUiState {
    object Loading : ModListUiState
    data class Error(val message: String) : ModListUiState
    class Loaded(
        val groups: List<ModGroupUiState>,
    ) : ModListUiState
}
