package com.mineinabyss.launchy.config.data

import com.mineinabyss.launchy.auth.data.ProfileModel
import kotlinx.serialization.Serializable


@Serializable
data class Config(
    val handledImportOptions: Boolean = false,
    val onboardingComplete: Boolean = false,
    val currentProfile: ProfileModel? = null,
    val javaPath: String? = null,
    val jvmArguments: String? = null,
    val memoryAllocation: Int? = null,
    val useRecommendedJvmArguments: Boolean = true,
    val preferHue: Float? = null,
    val startInFullscreen: Boolean = false,
    val lastPlayedMap: Map<String, Long> = mapOf(),
)
