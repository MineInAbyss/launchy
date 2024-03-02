package com.mineinabyss.launchy.logic

import com.mineinabyss.launchy.data.modpacks.PackDependencies
import com.mineinabyss.launchy.state.LaunchyState
import org.to2mbn.jmccc.launch.LauncherBuilder
import org.to2mbn.jmccc.mcdownloader.MinecraftDownloaderBuilder
import org.to2mbn.jmccc.mcdownloader.download.concurrent.CallbackAdapter
import org.to2mbn.jmccc.mcdownloader.provider.DownloadProviderChain
import org.to2mbn.jmccc.mcdownloader.provider.fabric.FabricDownloadProvider
import org.to2mbn.jmccc.option.LaunchOption
import org.to2mbn.jmccc.option.MinecraftDirectory
import org.to2mbn.jmccc.version.Version


object Launcher {
    fun launch(state: LaunchyState) {
        val packState = state.modpackState ?: return
        val dir = MinecraftDirectory(packState.modpackDir.toFile())
        val launcher = LauncherBuilder.buildDefault()

        // Auth or show dialog
        when (val session = state.profile.currentSession) {
            null -> Auth.authOrShowDialog(state.profile, onComlete = { launch(state) })
            else -> state.currentLaunchProcess = launcher.launch(
                LaunchOption(
                    packState.modpack.dependencies.fullVersionName,
                    session,
                    dir
                )
            )
        }
    }

    fun download(
        deps: PackDependencies,
        dir: MinecraftDirectory,
        finishedDownload: (String) -> Unit
    ) {
        val downloader = when {
            deps.fabricLoader != null -> fabricDownloader()
            else -> vanillaDownloader()
        }
        val callback = object : CallbackAdapter<Version>() {
            override fun done(result: Version) {
                finishedDownload("${result.type} $result")
                downloader.shutdown()
            }

            override fun failed(e: Throwable) {
                e.printStackTrace()
                downloader.shutdown()
            }
        }

        downloader.downloadIncrementally(dir, deps.fullVersionName, callback)
    }


    fun vanillaDownloader() = MinecraftDownloaderBuilder.buildDefault()

    fun fabricDownloader() = MinecraftDownloaderBuilder.create().providerChain(
        DownloadProviderChain.create().addProvider(FabricDownloadProvider())
    ).build()
}
