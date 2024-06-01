package com.mineinabyss.launchy.instance.ui

import androidx.lifecycle.ViewModel
import com.mineinabyss.launchy.instance.data.GameInstanceDataSource
import com.mineinabyss.launchy.util.AppDispatchers
import com.mineinabyss.launchy.util.ModID
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import org.to2mbn.jmccc.mcdownloader.download.Downloader

class InstanceViewModel(
    val downloader: Downloader,
) : ViewModel() {
    val modsState: StateFlow<ModListUiState> get() = _modsState
    val installState = MutableStateFlow<InstallState>(InstallState.InProgress)

    private val instance = MutableStateFlow<GameInstanceDataSource?>(null)
    private val _modsState = MutableStateFlow<ModListUiState>(ModListUiState.Loading)
    val installQueueState = MutableStateFlow<InstallState>(InstallState.InProgress)

    @OptIn(ExperimentalCoroutinesApi::class)
    val modList = instance.mapLatest {
        withContext(AppDispatchers.IO) {
            it?.loadModList()
        }
    }

    //TODO read
    val userInstalledMods = MutableStateFlow<List<ModUiState>>(emptyList())

    val enabledMods = MutableStateFlow(listOf<ModID>())
//    val instanceUIState =
//    val modpack: Modpack
//    val modpackConfig: InstanceUserConfig

        // trigger update incase we have dependencies
//        enabledMods.forEach { setModEnabled(it, true) }


//    val availableMods = mutableStateSetOf<Mod>().apply {
//        addAll(modpack.mods.mods)
//    }
//    val enabledMods = mutableStateSetOf<Mod>().apply {
//        addAll(modpackConfig.toggledMods.mapNotNull { modpack.mods.getModById(it) })
//        val defaultEnabled = modpack.mods.groups
//            .filter { it.enabledByDefault }
//            .map { it.name } - modpackConfig.seenGroups
//        val fullEnabled = modpackConfig.fullEnabledGroups
//        val forceEnabled = modpack.mods.groups.filter { it.forceEnabled }.map { it.name }
//        val forceDisabled = modpack.mods.groups.filter { it.forceDisabled }
//        val fullDisabled = modpackConfig.fullDisabledGroups
//        addAll(((fullEnabled + defaultEnabled + forceEnabled).toSet())
//            .mapNotNull { modpack.mods.getGroup(it) }
//            .mapNotNull { modpack.mods.modGroups[it] }.flatten()
//        )
//        removeAll((forceDisabled + fullDisabled).toSet().mapNotNull { modpack.mods.modGroups[it] }.flatten().toSet())
//    }
//
//    val disabledMods: Set<Mod> by derivedStateOf { modpack.mods.mods - enabledMods }

//    val enabledModsWithConfig by derivedStateOf {
//        enabledMods.filter { it.info.configUrl != "" }
//    }
//
//    val enabledConfigs: MutableSet<Mod> = mutableStateSetOf<Mod>().apply {
//        addAll(modpackConfig.toggledConfigs.mapNotNull { modpack.mods.getModById(it) })
//    }

//    val queued = DownloadQueueState(userConfig, modpack, toggles)
//    val downloads = DownloadState()

    fun setModState(mod: ModID, enabled: Boolean) {
        if (enabled) enabledMods.value += mod
        else enabledMods.value -= mod
    }

    fun installMods() {
        TODO()
    }

    fun launch() {
        TODO()
    }
//    fun saveToConfig() {
//        userConfig.copy(
//            fullEnabledGroups = modpack.mods.modGroups
//                .filter { toggles.enabledMods.containsAll(it.value) }.keys
//                .map { it.name }.toSet(),
//            userAgreedDeps = queued.userAgreedModLoaders,
//            toggledMods = toggles.enabledMods.mapTo(mutableSetOf()) { it.modId },
//            toggledConfigs = toggles.enabledConfigs.mapTo(mutableSetOf()) { it.modId } + toggles.enabledMods.filter { it.info.forceConfigDownload }
//                .mapTo(mutableSetOf()) { it.info.name },
//            seenGroups = modpack.mods.groups.map { it.name }.toSet(),
//            modDownloadInfo = queued.modDownloadInfo,
////            configDownloadInfo = toggles.downloadConfigURLs.mapKeys { it.key.info.name },
//        ).save(instance.userConfigFile)
//    }

//    fun setModEnabled(mod: Mod, enabled: Boolean) {
//        if (enabled) {
//            enabledMods += mod
//            enabledMods.filter { !mod.compatibleWith(it) }
//                .forEach { setModEnabled(it, false) }
//            disabledMods.filter { it.info.name in mod.info.requires }.forEach { setModEnabled(it, true) }
//        } else {
//            enabledMods -= mod
//            // if a mod is disabled, disable all mods that depend on it
//            enabledMods.filter { it.info.requires.contains(mod.info.name) }.forEach { setModEnabled(it, false) }
//            // if a mod is disabled, and the dependency is only used by this mod, disable the dependency too, unless it's not marked as a dependency
//            enabledMods.filter { dep ->
//                mod.info.requires.contains(dep.info.name)  // if the mod depends on this dependency
//                        && dep.info.dependency // if the dependency is marked as a dependency
//                        && enabledMods.none { it.info.requires.contains(dep.info.name) }  // and no other mod depends on this dependency
////                        && !versions.modGroups.filterValues { it.contains(dep) }.keys.any { it.forceEnabled } // and the group the dependency is in is not force enabled
//            }.forEach { setModEnabled(it, false) }
//        }
//        setModConfigEnabled(mod, enabled)
//    }
//
//    fun setModConfigEnabled(mod: Mod, enabled: Boolean) {
//        if (mod.info.configUrl.isNotBlank() && enabled) enabledConfigs.add(mod)
//        else enabledConfigs.remove(mod)
//    }
}
