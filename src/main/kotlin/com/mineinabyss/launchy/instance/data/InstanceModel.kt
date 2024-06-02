package com.mineinabyss.launchy.instance.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.mineinabyss.launchy.instance.data.storage.InstanceConfig
import com.mineinabyss.launchy.instance.data.storage.InstanceUserConfig
import com.mineinabyss.launchy.util.Dirs
import com.mineinabyss.launchy.util.InstanceKey
import kotlinx.coroutines.Dispatchers
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.div
import kotlin.io.path.name

data class InstanceModel(
    val config: InstanceConfig,
    val userConfig: InstanceUserConfig,
    val directory: Path,
    val key: InstanceKey = InstanceKey(directory.name),
) {
    val instanceFile = directory / "instance.yml"
    val backupInstanceFile = directory / "instance-backup.yml"
    val overridesDir = directory / "overrides"
    val imageLoaderDispatcher = Dispatchers.IO.limitedParallelism(1)
    val modpackFilesDir = directory / "modpack"

    val minecraftDir = config.overrideMinecraftDir?.let { Path(it) } ?: Dirs.modpackDir(directory.name)

    val modsDir = (minecraftDir / "mods").createDirectories()
    val userMods = (minecraftDir / "modsFromUser").createDirectories()

    val downloadsDir: Path = minecraftDir / "launchyDownloads"
    val userConfigFile = (directory / "config.yml")
    val packDownloadFile = (downloadsDir / "pack")

    var updatesAvailable by mutableStateOf(false)
    var enabled: Boolean by mutableStateOf(true)

}
