package com.mineinabyss.launchy.logic

import com.mineinabyss.launchy.ui.screens.Dialog
import com.mineinabyss.launchy.ui.screens.dialog

fun <T> Result<T>.showDialogOnError(title: String? = null): Result<T> {
    onFailure { dialog = Dialog.fromException(it, title) }
    return this
}

fun <T> Result<T>.getOrShowDialog() = showDialogOnError().getOrNull()
