package com.mineinabyss.launchy.instance.ui

import com.mineinabyss.launchy.util.ModID

sealed interface InstallState {
    data object InProgress : InstallState
    data class Queued(
        val modLoaderChange: String?,
        val install: List<ModID>,
        val update: List<ModID>,
        val remove: List<ModID>,
        val failures: List<ModID>,
    ) : InstallState

    data object AllInstalled : InstallState
    data object Error : InstallState
}
