package com.mineinabyss.launchy.instance.ui

import com.mineinabyss.launchy.util.Option

data class ModGroupUiState(
    val id: String,
    val title: String,
    val enabled: Boolean,
    val force: Option,
    val mods: List<ModUiState>
)
