package com.mineinabyss.launchy.instance_list.data

import com.charleskorn.kaml.decodeFromStream
import com.charleskorn.kaml.encodeToStream
import com.mineinabyss.launchy.downloads.data.Downloader
import com.mineinabyss.launchy.instance.data.InstanceModel
import com.mineinabyss.launchy.instance.data.ModListModel
import com.mineinabyss.launchy.instance.data.storage.InstanceConfig
import com.mineinabyss.launchy.util.Formats
import java.nio.file.Path
import kotlin.io.path.*

class LocalInstancesDataSource(
    val rootDir: Path,
) {
    fun readInstances(): List<InstanceModel> = rootDir
        .listDirectoryEntries()
        .filter { it.isDirectory() }
        .mapNotNull { dir ->
            readInstance(dir / "instance.yml")
                .onFailure { it.printStackTrace() }
                .getOrNull()
        }

    fun readInstance(instanceFile: Path): Result<InstanceModel> = runCatching {
        InstanceModel(Formats.yaml.decodeFromStream<InstanceConfig>(instanceFile.inputStream()), instanceFile.parent)
    }

    fun saveInstance(instance: InstanceModel) {
        val file = instance.instanceFile
        if (file.exists()) {
            file.copyTo(instance.backupInstanceFile, overwrite = true)
        } else {
            file.createFile()
        }
        Formats.yaml.encodeToStream(instance, file.outputStream())
    }

    @OptIn(ExperimentalPathApi::class)
    fun deleteInstance(
        instance: InstanceModel,
        deleteMinecraftDir: Boolean
    ) {
        if (deleteMinecraftDir) instance.minecraftDir.deleteRecursively()
        instance.directory.deleteRecursively()
    }

    fun loadModList(instance: InstanceModel): ModListModel {
        TODO()
    }

    data class CloudInstanceWithHeaders(
        val config: InstanceConfig,
        val url: String,
        val headers: Downloader.ModifyHeaders,
    )

}
