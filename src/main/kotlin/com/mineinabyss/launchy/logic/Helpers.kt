package com.mineinabyss.launchy.logic

import com.mineinabyss.launchy.ui.screens.Dialog
import com.mineinabyss.launchy.ui.screens.dialog

fun <T> Result<T>.showDialogOnError(): Result<T> {
    onFailure { dialog = Dialog.fromException(it) }
    return this
}

fun <T> Result<T>.getOrShowDialog() = showDialogOnError().getOrNull()
