package com.mineinabyss.launchy.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.mineinabyss.launchy.data.config.Config
import com.mineinabyss.launchy.data.config.PlayerProfile
import jmccc.microsoft.MicrosoftAuthenticator

class ProfileState(
    val config: Config
) {
    var authCode: String? by mutableStateOf(null)

    var currentSession: MicrosoftAuthenticator? by mutableStateOf(null)
    var currentProfile: PlayerProfile? by mutableStateOf(config.currentProfile)
}
