package com.mineinabyss.launchy.logic

import com.mineinabyss.launchy.data.modpacks.PackDependencies
import com.mineinabyss.launchy.state.ProfileState
import com.mineinabyss.launchy.state.modpack.ModpackState
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.to2mbn.jmccc.launch.LauncherBuilder
import org.to2mbn.jmccc.mcdownloader.MinecraftDownloaderBuilder
import org.to2mbn.jmccc.mcdownloader.download.concurrent.CallbackAdapter
import org.to2mbn.jmccc.mcdownloader.provider.DownloadProviderChain
import org.to2mbn.jmccc.mcdownloader.provider.fabric.FabricDownloadProvider
import org.to2mbn.jmccc.option.LaunchOption
import org.to2mbn.jmccc.option.MinecraftDirectory
import org.to2mbn.jmccc.version.Version
import java.nio.file.Path
import kotlin.io.path.createParentDirectories


object Launcher {
    suspend fun launch(pack: ModpackState, profile: ProfileState): Unit = coroutineScope {
        val dir = MinecraftDirectory(pack.modpackDir.toFile())
        val launcher = LauncherBuilder.buildDefault()

        // Auth or show dialog
        when (val session = profile.currentSession) {
            null -> Auth.authOrShowDialog(profile) {
                launch { launch(pack, profile) }
            }
            else -> pack.currentLaunchProcess = launcher.launch(
                LaunchOption(
                    pack.modpack.dependencies.fullVersionName,
                    session,
                    dir
                )
            )
        }
    }

    fun download(
        deps: PackDependencies,
        minecraftDir: Path,
        finishedDownload: (String) -> Unit
    ) {
        minecraftDir.createParentDirectories()
        val dir = MinecraftDirectory(minecraftDir.toFile())

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
