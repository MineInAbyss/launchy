package com.mineinabyss.launchy.instance.ui

import com.mineinabyss.launchy.util.Option

data class ModGroupInteractions(
    val onToggleGroup: (Option) -> Unit,
)

data class ModInteractions(
    val onToggleMod: (Boolean) -> Unit,
)
