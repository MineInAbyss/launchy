package com.mineinabyss.launchy.instance_list.ui

import androidx.lifecycle.ViewModel
import com.mineinabyss.launchy.config.data.GameInstance
import kotlinx.coroutines.flow.MutableStateFlow

class InstanceListViewModel : ViewModel() {
    val gameInstances = MutableStateFlow(listOf<GameInstance>())
    val lastPlayed = MutableStateFlow(mapOf<String, Long>())
}
