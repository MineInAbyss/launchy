package com.mineinabyss.launchy.instance.ui

import com.mineinabyss.launchy.util.ModID
import com.mineinabyss.launchy.util.Option

class ModListInteractions(
    val onToggleGroup: (Option) -> Unit,
    val onToggleMod: (ModID, Boolean) -> Unit,
)
