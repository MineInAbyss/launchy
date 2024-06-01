package com.mineinabyss.launchy.auth.data

import com.mineinabyss.launchy.auth.data.identity.IdentityDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import net.raphimc.minecraftauth.step.java.session.StepFullJavaSession.FullJavaSession

class ProfileRepository(
    val identity: IdentityDataSource,
) {
    private val _authRequest = MutableStateFlow<IdentityDataSource.VerificationRequired?>(null)

    private val _currentSession = MutableStateFlow<FullJavaSession?>(null)
    private val _currentProfile = MutableStateFlow<ProfileModel?>(null)

    val authRequest = _authRequest.asStateFlow()
    val currentProfile = _currentProfile.asStateFlow()

    fun useProfile(config: ProfileModel) {
        _currentProfile.update { config }
        _currentSession.update { null }
    }

    fun logout() {
        val uuid = _currentProfile.value?.uuid ?: return
        identity.forgetSession(uuid)
        _currentProfile.update { null }
        _currentSession.update { null }
    }

    suspend fun authenticateCurrentProfile() = withContext(Dispatchers.IO) {
        val profile = _currentProfile.value
        val session = identity.authFlow(
            profile,
            onVerificationRequired = { verification ->
                _authRequest.update { verification }
            }
        )
        _currentProfile.update { ProfileModel(session.mcProfile.name, session.mcProfile.id) }
        _currentSession.update { session }
    }
}
