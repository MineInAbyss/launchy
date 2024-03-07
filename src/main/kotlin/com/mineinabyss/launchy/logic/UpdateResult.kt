package com.mineinabyss.launchy.logic

sealed interface UpdateResult {
    object UpToDate : UpdateResult
    object HasUpdates : UpdateResult
    object NotCached : UpdateResult
}
