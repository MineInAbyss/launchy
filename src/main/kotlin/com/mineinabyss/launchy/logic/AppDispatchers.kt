package com.mineinabyss.launchy.logic

import com.mineinabyss.launchy.ui.screens.Dialog
import com.mineinabyss.launchy.ui.screens.dialog
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

object AppDispatchers {
    @OptIn(ExperimentalCoroutinesApi::class)
    val IOContext = Dispatchers.IO.limitedParallelism(10)

    /** IO Dispatcher that won't get cancelled when a composable goes off screen. */
    val IO = CoroutineScope(IOContext)

    @OptIn(ExperimentalCoroutinesApi::class)
    val profileLaunch = CoroutineScope(IOContext.limitedParallelism(1))


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
