package com.mineinabyss.launchy.logic

import com.mineinabyss.launchy.data.Constants
import com.mineinabyss.launchy.data.Formats
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

sealed interface AppUpdateState {
    data object Unknown : AppUpdateState
    data object NoUpdate : AppUpdateState
    data class UpdateAvailable(val release: GithubRelease) : AppUpdateState
}

object GithubUpdateChecker {
    suspend fun checkForUpdates(): AppUpdateState = runCatching {
        val currVersion = Constants.APP_VERSION ?: return AppUpdateState.Unknown
        val currTag = "v$currVersion"
        val current = Downloader.httpClient.get {
            url("https://api.github.com/repos/${Constants.GITHUB_REPO}/releases/tags/$currTag")
        }
        val currentRelease = if (current.status != HttpStatusCode.OK) null
        else Formats.json.decodeFromString<GithubRelease>(current.bodyAsText())

        val latest = if (currentRelease?.prerelease == true) getLatestPrerelease()
        else getLatestStableRelease()
        println("Current $currentRelease")
        println("Latest $latest")
        return if (latest.tag_name == currTag) AppUpdateState.NoUpdate else AppUpdateState.UpdateAvailable(latest)
    }.getOrDefault(AppUpdateState.Unknown)

    suspend fun getLatestPrerelease(): GithubRelease {
        val response = Downloader.httpClient.get {
            url("https://api.github.com/repos/${Constants.GITHUB_REPO}/releases")
        }

        val releases = Formats.json.decodeFromString<List<GithubRelease>>(response.bodyAsText())
        return releases.maxBy { it.published_at }
    }

    suspend fun getLatestStableRelease(): GithubRelease {
        val response = Downloader.httpClient.get {
            url("https://api.github.com/repos/${Constants.GITHUB_REPO}/releases/latest")
        }
        return Formats.json.decodeFromString<GithubRelease>(response.bodyAsText())
    }
}


@Serializable
data class GithubRelease(
    val published_at: String,
    val tag_name: String,
    val html_url: String,
    val prerelease: Boolean,
)
