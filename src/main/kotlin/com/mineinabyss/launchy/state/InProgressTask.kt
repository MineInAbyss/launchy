package com.mineinabyss.launchy.state

open class InProgressTask(val name: String) {
    class WithPercentage(
        name: String,
        val current: Long,
        val total: Long,
        val measurement: String?,
    ) : InProgressTask(name)
}
