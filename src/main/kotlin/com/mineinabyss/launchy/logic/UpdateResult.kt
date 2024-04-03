package com.mineinabyss.launchy.logic

sealed interface UpdateResult {
    val headers: Downloader.ModifyHeaders

    data class UpToDate(
        override val headers: Downloader.ModifyHeaders
    ) : UpdateResult

    data class HasUpdates(
        override val headers: Downloader.ModifyHeaders
    ) : UpdateResult

    data class NotCached(
        override val headers: Downloader.ModifyHeaders
    ) : UpdateResult
}
