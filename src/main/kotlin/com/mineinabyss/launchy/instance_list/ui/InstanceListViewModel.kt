package com.mineinabyss.launchy.instance_list.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mineinabyss.launchy.instance.data.GameInstanceDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class InstanceListViewModel : ViewModel() {
    init {
        viewModelScope.launch {

        }
    }

    val gameInstances = MutableStateFlow(listOf<GameInstanceDataSource>())
    val lastPlayed = MutableStateFlow(mapOf<String, Long>())
}
