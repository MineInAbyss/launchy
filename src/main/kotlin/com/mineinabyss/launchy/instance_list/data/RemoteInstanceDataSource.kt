package com.mineinabyss.launchy.instance_list.data

import com.mineinabyss.launchy.downloads.data.Downloader
import com.mineinabyss.launchy.instance.data.storage.InstanceConfig
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class RemoteInstanceDataSource {
    suspend fun getRemoteInstance(
        url: Url
    ): Result<InstanceConfig> = runCatching {
        //TODO cache by headers
        Downloader.httpClient.get(url).body<InstanceConfig>()
    }

    suspend fun fetchUpdatesForInstance(
        instance: InstanceConfig,
        url: Url,
    ): Result<InstanceConfig> = getRemoteInstance(url).mapCatching { remote ->
        instance.copy(
            description = remote.description,
            backgroundURL = remote.backgroundURL,
            logoURL = remote.logoURL,
            hue = remote.hue,
            source = remote.source,
        )
    }
}
