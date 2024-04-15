package com.mineinabyss.launchy.ui.elements

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowScope
import com.mineinabyss.launchy.ui.state.TopBarProvider

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
