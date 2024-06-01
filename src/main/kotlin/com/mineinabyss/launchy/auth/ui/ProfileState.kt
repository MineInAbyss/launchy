package com.mineinabyss.launchy.auth.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.mineinabyss.launchy.auth.data.PlayerProfile
import com.mineinabyss.launchy.config.data.Config
import net.raphimc.minecraftauth.step.java.session.StepFullJavaSession.FullJavaSession

class ProfileState(
    val config: Config
) {
    var authCode: String? by mutableStateOf(null)

    var currentSession: FullJavaSession? by mutableStateOf(null)
    var currentProfile: PlayerProfile? by mutableStateOf(config.currentProfile)
}
