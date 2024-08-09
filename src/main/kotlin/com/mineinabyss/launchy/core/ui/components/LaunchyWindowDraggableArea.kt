package com.mineinabyss.launchy.core.ui.components

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.window.WindowScope
import com.mineinabyss.launchy.core.ui.TopBarProvider

@Composable
fun WindowScope.BetterWindowDraggableArea(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {}
) {
    val topBar = TopBarProvider.current
    WindowDraggableArea(modifier.pointerInput(Unit) {
        detectDragGestures(onDragStart = {
            topBar.ensureFloating()
        }) { _, _ -> }
    }) {
        content()
    }
}
