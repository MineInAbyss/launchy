package com.mineinabyss.launchy.data

import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.*
import com.mineinabyss.launchy.logic.Downloader
import com.mineinabyss.launchy.util.Option
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
    val groups = mutableStateMapOf<GroupName, Option>().apply { putAll(config.groups) }
    val toggledMods = mutableStateSetOf<Mod>().apply { addAll(config.toggledMods.mapNotNull { it.toMod() }) }
    val enabledMods: Set<Mod> by derivedStateOf {
        modsInGroup(Option.ENABLED) + toggledMods - modsInGroup(Option.DISABLED)
    }
    val disabledMods: Set<Mod> by derivedStateOf { versions.mods.values.toSet() - enabledMods }

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
        if (enabled) toggledMods += mod
        else toggledMods -= mod
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

    fun modsInGroup(option: Option) = groups
        // Add all mods from enabled groups
        .filterValues { it == option }
        .mapNotNull { versions.modGroups[it.key] }
        .flatten()
        .toSet()

    fun save() {
        config.copy(
            groups = groups,
            toggledMods = toggledMods.mapTo(mutableSetOf()) { it.name },
            downloads = downloads.mapKeys { it.key.name }
        ).save()
    }

    fun ModName.toMod(): Mod? = versions.mods[this]

    val Mod.file get() = Dirs.mods / "${name}.jar"
    val Mod.isDownloaded get() = file.exists()

    private fun updateNotPresent() {
        notPresentDownloads = downloads.filter { !it.key.isDownloaded }.keys
    }
}

fun <T> mutableStateSetOf() = Collections.newSetFromMap(mutableStateMapOf<T, Boolean>())
