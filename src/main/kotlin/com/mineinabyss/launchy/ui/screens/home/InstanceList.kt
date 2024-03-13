package com.mineinabyss.launchy.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.data.config.GameInstance

@Composable
fun InstanceList(title: String, packs: List<GameInstance>) {
    val state = LocalLaunchyState
    Column {
//        var showAll by remember { mutableStateOf(false) }
        val visiblePacks = packs//.take(6)
        Row {
            Text(title, style = MaterialTheme.typography.headlineMedium)
        }
        Spacer(Modifier.height(8.dp))
        if (visiblePacks.isEmpty()) {
            Text("No instances installed yet, click the + button on the sidebar to add one!")
        } else BoxWithConstraints(Modifier) {
            val total = packs.size + 1
            val colums = ((maxWidth / InstanceCardStyle.cardWidth).toInt()).coerceAtMost(total).coerceAtLeast(1)
            val rows = (total / colums).coerceAtLeast(1)
            val lazyGridState = rememberLazyGridState()
            LazyVerticalGrid(
                userScrollEnabled = false,
                state = lazyGridState,
                columns = GridCells.Fixed(colums),
                modifier = Modifier
                    .width((16.dp + InstanceCardStyle.cardWidth) * total)
                    .heightIn(max = (16.dp + InstanceCardStyle.cardHeight) * rows),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(visiblePacks.sortedByDescending { state.lastPlayed[it.config.name] }) { pack ->
                    InstanceCard(pack.config, pack)
                }
            }
        }
    }
}