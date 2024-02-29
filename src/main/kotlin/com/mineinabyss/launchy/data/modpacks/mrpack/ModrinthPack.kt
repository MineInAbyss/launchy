package com.mineinabyss.launchy.data.modpacks.mrpack

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ModrinthPack(
    val dependencies: Dependencies,
) {
    @Serializable
    data class Dependencies(
        val minecraft: String,
        @SerialName("fabric-loader")
        val fabricLoader: String? = null,
        @SerialName("quilt-loader")
        val quiltLoader: String? = null,
        val forge: String? = null,
        val neoforge: String? = null,
    ) {
        @Transient
        val fullVersionName = when {
            fabricLoader != null -> "fabric-loader-$fabricLoader-$minecraft"
            quiltLoader != null -> "quilt-loader-$quiltLoader-$minecraft"
            forge != null -> "forge-$forge-$minecraft"
            neoforge != null -> "neoforge-$neoforge-$minecraft"
            else -> minecraft
        }
    }
}

