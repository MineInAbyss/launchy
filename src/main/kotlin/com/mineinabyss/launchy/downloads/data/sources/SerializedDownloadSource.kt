package com.mineinabyss.launchy.downloads.data.sources

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.core.scope.Scope

@Serializable
sealed interface SerializedDownloadSource {
    fun getDataSource(scope: Scope): ModpackDataSource

    @Serializable
    @SerialName("local")
    class Local : SerializedDownloadSource {
        override fun getDataSource(scope: Scope) = LocalModpackDataSource()
    }

    @SerialName("downloadFromURL")
    @Serializable
    class DownloadFromURL(val url: String) : SerializedDownloadSource {
        override fun getDataSource(scope: Scope) = URLModpackDataSource(scope.get(), url)
    }
}
