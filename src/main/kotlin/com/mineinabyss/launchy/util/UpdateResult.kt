package com.mineinabyss.launchy.util

import com.mineinabyss.launchy.core.data.Downloader

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
