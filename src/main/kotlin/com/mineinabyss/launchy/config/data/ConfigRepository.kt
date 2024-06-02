package com.mineinabyss.launchy.config.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ConfigRepository(
    val dataSource: ConfigDataSource
) {
    private val _config = MutableStateFlow(Config())

    val config = _config.asStateFlow()

    fun updateConfig(config: Config) {
        _config.value = config
        dataSource.saveConfig(config)
    }

    fun tryLoadConfig() {
        _config.value = dataSource.readConfig().getOrElse { return }
    }
}
