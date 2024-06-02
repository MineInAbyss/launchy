package com.mineinabyss.launchy.downloads.data.sources

import com.mineinabyss.launchy.downloads.data.Downloader
import com.mineinabyss.launchy.instance.data.InstanceModel
import java.nio.file.Path

class URLModpackDataSource(
    val downloader: Downloader,
    val resourceURL: String,
) : ModpackDataSource {
    override suspend fun fetchLatestModsFor(instance: InstanceModel): Path? {
        downloader.download(
            resourceURL,
            instance.packDownloadFile,
            options = Downloader.Options(saveModifyHeadersFor = instance)
        )
        return instance.packDownloadFile
    }
}
