package com.mineinabyss.launchy.data

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.json.Json

object Formats {
    val yaml = Yaml(
        configuration = YamlConfiguration(
            strictMode = false,
        )
    )

    val json = Json {
        ignoreUnknownKeys = true
    }
}
