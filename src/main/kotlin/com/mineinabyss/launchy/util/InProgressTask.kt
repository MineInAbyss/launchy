package com.mineinabyss.launchy.util

open class InProgressTask(val name: String) {
    class WithPercentage(
        name: String,
        val current: Long,
        val total: Long,
        val measurement: String?,
    ) : InProgressTask(name)

    companion object {
        fun bytes(name: String, current: Long, total: Long) =
            WithPercentage(name, current / 1000000, total / 1000000, "MB")
    }
}
