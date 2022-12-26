package com.mineinabyss.launchy.ui.screens.settings

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.LocalLaunchyState
import kotlin.math.roundToInt

@Composable
@Preview
fun JavaScreen() {
    val state = LocalLaunchyState
    Scaffold { paddingValues ->
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
                modifier = Modifier.padding(5.dp)
            ) {
                Box(
                    Modifier.padding(paddingValues).padding(start = 10.dp, top = 40.dp)
                ) {
                    val lazyListState = rememberLazyListState()
                    LazyColumn(Modifier.fillMaxSize().padding(end = 12.dp), lazyListState) {
                        item("java settings") {
                            val minRam = SliderSwitch(label = "Minimum RAM", valueRange = 1..12).roundToInt()
                            val maxRam = SliderSwitch(label = "Maximum RAM:", valueRange = 1..12).roundToInt()
                            // Figure out way to handle this, probably storing via state or something
                            state.clientSettings = ClientSettings(state.clientSettings.minecraft, JavaSettings(minRam, maxRam))
                        }
                    }
                    VerticalScrollbar(
                        modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd),
                        adapter = rememberScrollbarAdapter(lazyListState)
                    )
                }
            }
        }
    }
}
