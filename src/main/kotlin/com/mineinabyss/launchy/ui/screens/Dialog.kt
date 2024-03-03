package com.mineinabyss.launchy.ui.screens

sealed interface Dialog {
    object None : Dialog
    object Auth : Dialog

    class Error(val title: String, val message: String) : Dialog
}
