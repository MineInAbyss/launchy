package com.mineinabyss.launchy.instance.ui.screens

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mineinabyss.launchy.core.ui.Constants
import com.mineinabyss.launchy.instance.ui.InstanceViewModel
import com.mineinabyss.launchy.instance.ui.ModListUiState
import com.mineinabyss.launchy.instance.ui.components.settings.ModGroup
import com.mineinabyss.launchy.instance.ui.components.settings.infobar.InfoBar

@Composable
fun ModManagementTab(
    instance: InstanceViewModel = viewModel()
) {
    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = { InfoBar() },
    ) { paddingValues ->
        val modsState by instance.modsState.collectAsState()
        val groups = when (modsState) {
            is ModListUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                return@Scaffold
            }

            is ModListUiState.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error loading mods: ${(modsState as ModListUiState.Error).message}")
                }
                return@Scaffold
            }

            is ModListUiState.Loaded -> (modsState as ModListUiState.Loaded).groups
        }
        Box(Modifier.padding(paddingValues)) {
            Box(Modifier.padding(horizontal = Constants.SETTINGS_HORIZONTAL_PADDING)) {
                val userMods by instance.userInstalledMods.collectAsState()
                val lazyListState = rememberLazyListState()
                LazyColumn(Modifier.fillMaxSize().padding(end = 12.dp), lazyListState) {
                    item { Spacer(Modifier.height(4.dp)) }
                    items(groups) { group ->
                        val groupInteractions = instance.groupInteractionsFor(group.id)
                        ModGroup(group, groupInteractions)
                    }
                    // TODO probably worth just merging into groups
                    userMods?.let {
                        item {
                            val groupInteractions = instance.groupInteractionsFor(it.id)
                            ModGroup(it, groupInteractions)
                        }
                    }
                }
                VerticalScrollbar(
                    modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd).padding(vertical = 2.dp),
                    adapter = rememberScrollbarAdapter(lazyListState)
                )
            }
        }
    }
}
