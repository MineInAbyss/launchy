package com.mineinabyss.launchy.data

import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.*
import com.mineinabyss.launchy.logic.Downloader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.io.path.deleteIfExists
import kotlin.io.path.div
import kotlin.io.path.exists

class LaunchyState(
    // Config should never be mutated unless it also updates UI state
    private val config: Config,
    // Versions are immutable, we don't care for reading
    val versions: Versions,
    val scaffoldState: ScaffoldState
) {
    val enabledMods = mutableStateSetOf<Mod>().apply {
        addAll(config.toggledMods.mapNotNull { it.toMod() })
        val defaults = versions.groups
            .filter { it.enabledByDefault }
            .map { it.name } - config.seenGroups
        val fullEnabled = config.fullEnabledGroups
        val forced = versions.groups.filter { it.forced }.map { it.name }
        addAll((fullEnabled + defaults + forced).toSet()
            .mapNotNull { it.toGroup() }
            .mapNotNull { versions.modGroups[it] }.flatten()
        )
    }

    val disabledMods: Set<Mod> by derivedStateOf { versions.nameToMod.values.toSet() - enabledMods }

    val downloads = mutableStateMapOf<Mod, DownloadURL>().apply {
        putAll(config.downloads
            .mapNotNull { it.key.toMod()?.to(it.value) }
            .toMap()
        )
    }
    var notPresentDownloads by mutableStateOf(setOf<Mod>())
        private set

    val upToDate: Set<Mod> by derivedStateOf {
        (downloads - notPresentDownloads).filter { (mod, url) -> mod.url == url }.keys
    }

    val queuedDownloads by derivedStateOf { enabledMods - upToDate }
    val queuedDeletions by derivedStateOf { disabledMods.filter { it.isDownloaded } }
    val downloading = mutableStateMapOf<Mod, Long>()
    val isDownloading by derivedStateOf { downloading.isNotEmpty() }

    fun setModEnabled(mod: Mod, enabled: Boolean) {
        if (enabled) enabledMods += mod
        else enabledMods -= mod
    }

    suspend fun downloadAndRemoveQueued() = coroutineScope {
        updateNotPresent()
        for (mod in queuedDownloads)
            launch(Dispatchers.IO) { download(mod) }
        for (mod in queuedDeletions) {
            launch(Dispatchers.IO) { mod.file.deleteIfExists() }
            queuedDeletions
        }
        updateNotPresent()
    }

    suspend fun download(mod: Mod) {
        runCatching {
            downloading[mod] = 0 //TODO download progress?
            Downloader.download(url = mod.url, writeTo = mod.file)
            downloading -= mod
            downloads[mod] = mod.url
            save()
        }.onFailure {
            scaffoldState.snackbarHostState.showSnackbar(
                "Failed to download ${mod.name}: ${it.localizedMessage}!", "OK"
            )
        }
    }

    fun save() {
        config.copy(
            fullEnabledGroups = versions.modGroups
                .filter { enabledMods.containsAll(it.value) }.keys
                .map { it.name }.toSet(),
            toggledMods = enabledMods.mapTo(mutableSetOf()) { it.name },
            downloads = downloads.mapKeys { it.key.name },
            seenGroups = versions.groups.map { it.name }.toSet()
        ).save()
    }

    fun ModName.toMod(): Mod? = versions.nameToMod[this]
    fun GroupName.toGroup(): Group? = versions.nameToGroup[this]

    val Mod.file get() = Dirs.mods / "${name}.jar"
    val Mod.isDownloaded get() = file.exists()

    private fun updateNotPresent() {
        notPresentDownloads = downloads.filter { !it.key.isDownloaded }.keys
    }
}

fun <T> mutableStateSetOf() = Collections.newSetFromMap(mutableStateMapOf<T, Boolean>())
