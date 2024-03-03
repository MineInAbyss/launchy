package com.mineinabyss.launchy.ui.screens.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.data.modpacks.ModpackInfo

@Composable
fun ModpackGroup(title: String, packs: List<ModpackInfo>) {
    Column {
        var showAll by remember { mutableStateOf(false) }
        val visiblePacks = if (showAll) packs else packs.take(6)
        val arrowRotationState by animateFloatAsState(targetValue = if (showAll) 0f else -90f)
        Row {
            TextButton(onClick = { showAll = !showAll }) {
                Text(title, style = MaterialTheme.typography.headlineLarge)
                Icon(
                    Icons.Rounded.ArrowDropDown,
                    contentDescription = "Show all",
                    modifier = Modifier.size(32.dp).rotate(arrowRotationState)
                )
//                Text(if (showAll) "Show less" else "Show all")
            }
        }

        Surface(
            Modifier.fillMaxWidth(),
            tonalElevation = 2.dp,
            shape = RoundedCornerShape(20.dp)
        ) {
            BoxWithConstraints(Modifier.padding(16.dp)) {
                val total = packs.size
                val colums = ((maxWidth / ModpackCardStyle.cardWidth).toInt()).coerceAtMost(total).coerceAtLeast(1)
                val lazyGridState = rememberLazyGridState()
                LazyVerticalGrid(
                    state = lazyGridState,
                    columns = GridCells.Fixed(colums),
                    modifier = Modifier.width((16.dp + ModpackCardStyle.cardWidth) * total),/*.padding(end = 16.dp),*/
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(visiblePacks) { pack ->
                        ModpackCard(pack)
                    }
//                item {
//                    AddNewModpackCard()
//                }
                }
            }
        }
    }
}
