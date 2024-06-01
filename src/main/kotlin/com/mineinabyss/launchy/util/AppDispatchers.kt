package com.mineinabyss.launchy.util

import com.mineinabyss.launchy.core.ui.Dialog
import com.mineinabyss.launchy.core.ui.dialog
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

object AppDispatchers {
    /** IO Dispatcher that won't get cancelled when a composable goes off screen. */
    val IO = Dispatchers.IO

    @OptIn(ExperimentalCoroutinesApi::class)
    val profileLaunch = CoroutineScope(IO.limitedParallelism(1))

    fun CoroutineScope.launchOrShowDialog(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ): Job {
        return launch(context + SupervisorJob(), start, block).apply {
            invokeOnCompletion {
                if (it != null) dialog = Dialog.fromException(it)
            }
        }
    }
}
