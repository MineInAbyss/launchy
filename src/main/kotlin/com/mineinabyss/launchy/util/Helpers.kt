package com.mineinabyss.launchy.util

import com.mineinabyss.launchy.core.ui.Dialog
import com.mineinabyss.launchy.core.ui.screens.dialog
import java.util.*

fun <T> Result<T>.showDialogOnError(title: String? = null): Result<T> {
    onFailure {
        dialog = Dialog.fromException(it, title)
        it.printStackTrace()
    }
    return this
}

fun <T> Result<T>.getOrShowDialog() = showDialogOnError().getOrNull()


fun urlToFileName(url: String): String {
    return UUID.nameUUIDFromBytes(url.toByteArray()).toString()
}
