package com.mineinabyss.launchy.instance.ui

data class ModGroupUiState(
    val title: String,
    val enabled: Boolean,
    val forceEnabled: Boolean,
    val mods: List<ModUiState>
)
