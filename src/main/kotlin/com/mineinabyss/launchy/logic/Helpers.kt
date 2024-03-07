package com.mineinabyss.launchy.logic

import com.mineinabyss.launchy.ui.screens.Dialog
import com.mineinabyss.launchy.ui.screens.dialog
import java.util.*

fun <T> Result<T>.showDialogOnError(title: String? = null): Result<T> {
    onFailure { dialog = Dialog.fromException(it, title) }
    return this
}

fun <T> Result<T>.getOrShowDialog() = showDialogOnError().getOrNull()


fun urlToFileName(url: String): String {
    return UUID.nameUUIDFromBytes(url.toByteArray()).toString()
}
