package com.mineinabyss.launchy.core.data

import com.mineinabyss.launchy.util.InProgressTask
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TasksRepository {
    private val _inProgress = MutableStateFlow(mapOf<String, InProgressTask>())
    val inProgress = _inProgress.asStateFlow()

    fun start(key: String, task: InProgressTask) {
        _inProgress.update { it.plus(key to task) }
    }

    fun finish(key: String) {
        _inProgress.update { it.minus(key) }
    }

    inline fun <T> run(key: String, task: InProgressTask, run: () -> T): T {
        try {
            start(key, task)
            return run()
        } finally {
            finish(key)
        }
    }
}
