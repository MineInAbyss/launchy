package com.mineinabyss.launchy.logic

import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.*
import com.mineinabyss.launchy.data.*
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

    val downloadURLs = mutableStateMapOf<Mod, DownloadURL>().apply {
        putAll(config.downloads
            .mapNotNull { it.key.toMod()?.to(it.value) }
            .toMap()
        )
    }
    var installedFabricVersion by mutableStateOf(config.installedFabricVersion)

    var notPresentDownloads by mutableStateOf(setOf<Mod>())
        private set

    init {
        updateNotPresent()
    }

    val upToDate: Set<Mod> by derivedStateOf {
        (downloadURLs - notPresentDownloads).filter { (mod, url) -> mod.url == url }.keys
    }

    val queuedDownloads by derivedStateOf { enabledMods - upToDate }
    val queuedUpdates by derivedStateOf { queuedDownloads.filter { it.isDownloaded }.toSet() }
    val queuedInstalls by derivedStateOf { queuedDownloads - queuedUpdates }
    private var _deleted by mutableStateOf(0)
    val queuedDeletions by derivedStateOf {
        _deleted
        disabledMods.filter { it.isDownloaded }.also { if(it.isEmpty()) updateNotPresent() }
    }

    val downloading = mutableStateMapOf<Mod, Long>()
    val isDownloading by derivedStateOf { downloading.isNotEmpty() }

    var installingProfile by mutableStateOf(false)
    val fabricUpToDate by derivedStateOf {
        installedFabricVersion == versions.fabricVersion && FabricInstaller.isProfileInstalled(
            Dirs.minecraft,
            "Mine in Abyss"
        )
    }

    fun setModEnabled(mod: Mod, enabled: Boolean) {
        if (enabled) enabledMods += mod
        else enabledMods -= mod
    }

    suspend fun install() = coroutineScope {
        updateNotPresent()
        if (!fabricUpToDate)
            installFabric()
        for (mod in queuedDownloads)
            launch(Dispatchers.IO) {
                download(mod)
                updateNotPresent()
            }
        for (mod in queuedDeletions) {
            launch(Dispatchers.IO) {
                mod.file.deleteIfExists()
                _deleted++
            }
        }
    }


    fun installFabric() {
        installingProfile = true
        FabricInstaller.installToLauncher(
            Dirs.minecraft,
            Dirs.mineinabyss,
            "Mine in Abyss",
            versions.minecraftVersion,
            "fabric-loader",
            versions.fabricVersion,
        )
        installingProfile = false
        installedFabricVersion = "Installing..."
        installedFabricVersion = versions.fabricVersion
    }

    suspend fun download(mod: Mod) {
        runCatching {
            downloading[mod] = 0 //TODO download progress?
            Downloader.download(url = mod.url, writeTo = mod.file)
            downloading -= mod
            downloadURLs[mod] = mod.url
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
            downloads = downloadURLs.mapKeys { it.key.name },
            seenGroups = versions.groups.map { it.name }.toSet(),
            installedFabricVersion = installedFabricVersion
        ).save()
    }

    fun ModName.toMod(): Mod? = versions.nameToMod[this]
    fun GroupName.toGroup(): Group? = versions.nameToGroup[this]

    val Mod.file get() = Dirs.mods / "${name}.jar"
    val Mod.isDownloaded get() = file.exists()

    private fun updateNotPresent(): Set<Mod> {
        return downloadURLs.filter { !it.key.isDownloaded }.keys.also { notPresentDownloads = it }
    }
}

fun <T> mutableStateSetOf() = Collections.newSetFromMap(mutableStateMapOf<T, Boolean>())
