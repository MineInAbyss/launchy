package com.mineinabyss.launchy.instance.ui

import com.mineinabyss.launchy.downloads.data.formats.Modpack

sealed interface ModListUiState {
    object Loading : ModListUiState
    data class Error(val message: String) : ModListUiState
    class Loaded(
        val groups: Modpack,
    ) : ModListUiState
}
