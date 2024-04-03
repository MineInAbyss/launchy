package com.mineinabyss.launchy.logic

data class Progress(val bytesDownloaded: Long, val totalBytes: Long, val timeElapsed: Long) {
    val percent: Float
        get() = bytesDownloaded.toFloat() / totalBytes.toFloat()
}
