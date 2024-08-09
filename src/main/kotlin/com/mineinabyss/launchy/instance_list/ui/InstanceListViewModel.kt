package com.mineinabyss.launchy.instance_list.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mineinabyss.launchy.instance.ui.InstanceUiState
import com.mineinabyss.launchy.instance_list.data.InstanceCardInteractions
import com.mineinabyss.launchy.instance_list.data.InstanceRepository
import com.mineinabyss.launchy.util.InstanceKey
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class InstanceListViewModel(
    val instanceRepo: InstanceRepository,
) : ViewModel() {
    val instances = instanceRepo.instances.combine(instanceRepo.lastPlayed) { inst, lastPlayed ->
        inst.values.sortedBy { lastPlayed[it.key] }
            .map { (config, userConfig, dir, key) ->
                InstanceUiState(
                    title = config.name,
                    description = config.description,
                    isCloudInstance = config.cloudInstanceURL != null,
                    // TODO
                    logo = null,
                    background = null,
                    runningProcess = null,
                    enabled = true,
                    updatesAvailable = false,
                    hue = 0f,
                    key = key,
                    installedModLoader = userConfig.userAgreedDeps?.fullVersionName,
                )
            }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        merge(instanceRepo.instances, instanceRepo.lastPlayed).map {
        }
        viewModelScope.launch {
            instanceRepo.loadLocalInstances()
        }
    }

    fun cardInteractionsFor(key: InstanceKey): InstanceCardInteractions {
        return InstanceCardInteractions(
            onOpen = { TODO() },
            onPlay = { TODO() }
        )
    }
}
