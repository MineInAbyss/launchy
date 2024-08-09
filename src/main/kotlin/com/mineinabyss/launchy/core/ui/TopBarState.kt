package com.mineinabyss.launchy.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowScope
import androidx.compose.ui.window.WindowState
import com.mineinabyss.launchy.util.OS
import java.awt.Dimension
import java.awt.Point
import java.awt.Toolkit

val TopBarProvider = compositionLocalOf<TopBarState> { error("No top bar provided") }
val TopBar: TopBarState
    @Composable
    get() = TopBarProvider.current

val windowScope: WindowScope
    @Composable
    get() = TopBar.windowScope

class TopBarState(
    val onClose: () -> Unit,
    val windowState: WindowState,
    val windowScope: WindowScope,
) {
    var floatingWindowSize: WindowSize? = null

    fun ensureMaximized() {
        when (OS.get()) {
            OS.WINDOWS -> {
                if (floatingWindowSize != null) return
                val window = windowScope.window
                val graphicsConfiguration = window.graphicsConfiguration
                val insets = Toolkit.getDefaultToolkit().getScreenInsets(graphicsConfiguration)
                val bounds = graphicsConfiguration.bounds
                floatingWindowSize = WindowSize(window.size, window.location)
                window.setSize(bounds.width, bounds.height - insets.bottom)
                window.setLocation(bounds.x, bounds.y)
            }

            else -> {
                windowState.placement = WindowPlacement.Maximized
            }

        }
    }

    fun ensureFloating() {
        when (OS.get()) {
            OS.WINDOWS -> {
                if (floatingWindowSize == null) return
                val window = windowScope.window
                floatingWindowSize?.let {
                    window.size = it.size
                    window.location = it.location
                    floatingWindowSize = null
                }
            }

            else -> {
                windowState.placement = WindowPlacement.Floating
            }
        }
    }

    fun toggleMaximized() = when (OS.get()) {
        OS.WINDOWS -> {
            if (floatingWindowSize == null) ensureMaximized()
            else ensureFloating()
        }

        else -> {
            if (windowState.placement == WindowPlacement.Maximized)
                ensureFloating()
            else ensureMaximized()
        }
    }
}

class WindowSize(
    val size: Dimension,
    val location: Point,
)
