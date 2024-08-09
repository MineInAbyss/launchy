package com.mineinabyss.launchy.downloads.data.formats

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface SerializedPackFormat {
    fun getFormat(): ModpackFormat

    @Serializable
    @SerialName("launchy")
    data object Launchy : SerializedPackFormat {
        override fun getFormat(): ModpackFormat = LaunchyPackFormat()
    }

    @Serializable
    @SerialName("modrinth")
    data object Modrinth : SerializedPackFormat {
        override fun getFormat(): ModpackFormat = ModrinthPackFormat()
    }

    @Serializable
    @SerialName("extras")
    data class WithExtraInfo(val format: SerializedPackFormat) : SerializedPackFormat {
        override fun getFormat(): ModpackFormat = ExtraInfoFormat(format.getFormat())
    }
}
