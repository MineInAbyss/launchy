package com.mineinabyss.launchy.state

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import com.mineinabyss.launchy.data.config.Config
import jmccc.microsoft.MicrosoftAuthenticator

class ProfileState(
    val config: Config
) {
    var avatar: ImageBitmap? by mutableStateOf(null)

    var currentSession: MicrosoftAuthenticator? by mutableStateOf(null)
    val currentProfileUUID: String? by derivedStateOf {
        currentSession?.auth()?.uuid?.toString() ?: config.currentProfileUUID
    }
}
