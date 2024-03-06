package com.mineinabyss.launchy.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.mineinabyss.launchy.data.config.Config
import com.mineinabyss.launchy.data.config.PlayerProfile
import net.raphimc.minecraftauth.step.java.session.StepFullJavaSession.FullJavaSession

class ProfileState(
    val config: Config
) {
    var authCode: String? by mutableStateOf(null)

    var currentSession: FullJavaSession? by mutableStateOf(null)
    var currentProfile: PlayerProfile? by mutableStateOf(config.currentProfile)
}
