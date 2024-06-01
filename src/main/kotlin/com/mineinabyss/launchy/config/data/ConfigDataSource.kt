package com.mineinabyss.launchy.config.data

import com.charleskorn.kaml.decodeFromStream
import com.mineinabyss.launchy.util.Dirs
import com.mineinabyss.launchy.util.Formats
import kotlinx.serialization.encodeToString
import kotlin.io.path.inputStream
import kotlin.io.path.writeText

class ConfigDataSource {
    fun readConfig(): Result<Config> = runCatching {
        Formats.yaml.decodeFromStream<Config>(Dirs.configFile.inputStream())
    }.onFailure { it.printStackTrace() }

    fun saveConfig(config: Config) {
        Dirs.configFile.writeText(Formats.yaml.encodeToString(config))
    }
}
