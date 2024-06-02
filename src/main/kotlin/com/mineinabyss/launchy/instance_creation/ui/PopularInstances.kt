package com.mineinabyss.launchy.instance_creation.ui

import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.mineinabyss.launchy.core.ui.components.ComfyContent
import com.mineinabyss.launchy.core.ui.components.ComfyTitle

@Composable
fun PopularInstances() {
    val popularInstances = remember {
        listOf(
            ""
        )
    }
    ComfyTitle("Popular instances")
    ComfyContent {
        LazyRow {
            items(popularInstances) {
//                    InstanceCard(it, modifier = Modifier.padding(8.dp))
            }
        }
    }
}
