package com.mineinabyss.launchy.auth.ui

import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mineinabyss.launchy.auth.data.ProfileRepository
import com.mineinabyss.launchy.core.data.TasksRepository
import com.mineinabyss.launchy.core.ui.Dialog
import com.mineinabyss.launchy.core.ui.screens.dialog
import com.mineinabyss.launchy.downloads.data.Downloader
import com.mineinabyss.launchy.util.AppDispatchers
import com.mineinabyss.launchy.util.DesktopHelpers
import com.mineinabyss.launchy.util.Dirs
import com.mineinabyss.launchy.util.InProgressTask
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.io.path.inputStream

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val tasks: TasksRepository,
) : ViewModel() {
    private val _profile = MutableStateFlow<ProfileUiState?>(null)

    val profile = _profile.asStateFlow()

    init {
        viewModelScope.launch {
            profileRepository.authRequest.collectLatest { verification ->
                if (verification == null) return@collectLatest
                dialog = Dialog.Auth(verification)
                DesktopHelpers.browse(verification.redirectTo)
            }
        }
        viewModelScope.launch {
            profileRepository.currentProfile.collectLatest { profile ->
                if (profile == null) return@collectLatest
                //TODO separate state for loading avatar?
                val avatar = getAvatar(profile.uuid).getOrNull()
                _profile.value = ProfileUiState(profile.name, avatar)
            }
        }
    }

    fun authOrShowDialog() = viewModelScope.launch {
        tasks.run("auth", InProgressTask("Authenticating")) {
            val session = profileRepository.authenticateCurrentProfile()

            dialog = Dialog.None
        }
    }

    fun logout() {
        profileRepository.logout()
        _profile.value = null
    }

    private suspend fun getAvatar(uuid: UUID): Result<BitmapPainter> = withContext(AppDispatchers.IO) {
        runCatching {
            Downloader.downloadAvatar(uuid, Downloader.Options(overwrite = false))
            BitmapPainter(
                loadImageBitmap(Dirs.avatar(uuid).inputStream()),
                filterQuality = FilterQuality.None
            )
        }
    }
}
