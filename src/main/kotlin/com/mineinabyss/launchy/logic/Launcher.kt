package com.mineinabyss.launchy.logic

import com.mineinabyss.launchy.data.modpacks.PackDependencies
import com.mineinabyss.launchy.state.LaunchyState
import com.mineinabyss.launchy.state.ProfileState
import com.mineinabyss.launchy.state.modpack.ModpackState
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.to2mbn.jmccc.auth.AuthInfo
import org.to2mbn.jmccc.auth.Authenticator
import org.to2mbn.jmccc.launch.LauncherBuilder
import org.to2mbn.jmccc.mcdownloader.MinecraftDownloaderBuilder
import org.to2mbn.jmccc.mcdownloader.download.concurrent.CallbackAdapter
import org.to2mbn.jmccc.mcdownloader.download.concurrent.DownloadCallback
import org.to2mbn.jmccc.mcdownloader.download.tasks.DownloadTask
import org.to2mbn.jmccc.mcdownloader.provider.DownloadProviderChain
import org.to2mbn.jmccc.mcdownloader.provider.fabric.FabricDownloadProvider
import org.to2mbn.jmccc.option.LaunchOption
import org.to2mbn.jmccc.option.MinecraftDirectory
import org.to2mbn.jmccc.version.Version
import java.nio.file.Path
import java.util.*
import kotlin.io.path.createParentDirectories


object Launcher {
    suspend fun launch(state: LaunchyState, pack: ModpackState, profile: ProfileState): Unit = coroutineScope {
        val dir = MinecraftDirectory(pack.instance.minecraftDir.toFile())
        val launcher = LauncherBuilder.buildDefault()

        // Auth or show dialog
        when (val session = profile.currentSession) {
            null -> Auth.authOrShowDialog(profile) {
                launch { launch(state, pack, profile) }
            }

            else -> state.setProcessFor(pack.instance, launcher.launch(
                LaunchOption(
                    pack.modpack.dependencies.fullVersionName,
                    Authenticator {
                        return@Authenticator AuthInfo(
                            session.mcProfile.name,
                            session.mcProfile.mcToken.accessToken,
                            session.mcProfile.id,
                            Collections.emptyMap(),
                            "msa",
                            "unknown",
                        );
                    },
                    dir
                )
            ).apply {
                onExit().whenComplete { process, throwable ->
                    state.setProcessFor(pack.instance, null)
                }
            })
        }
    }

    fun download(
        deps: PackDependencies,
        minecraftDir: Path,
        onStartDownload: (String) -> Unit,
        onFinishDownload: (String) -> Unit
    ): Job {
        val downloadJob = Job()
        minecraftDir.createParentDirectories()
        val dir = MinecraftDirectory(minecraftDir.toFile())

        val downloader = when {
            deps.fabricLoader != null -> fabricDownloader()
            else -> vanillaDownloader()
        }
        val callback = object : CallbackAdapter<Version>() {
            override fun done(result: Version) {
                onFinishDownload("${result.type} $result")
                downloader.shutdown()
                downloadJob.complete()
            }

            override fun failed(e: Throwable) {
                e.printStackTrace()
                downloader.shutdown()
                downloadJob.complete()
            }

            override fun <R : Any?> taskStart(task: DownloadTask<R>?): DownloadCallback<R>? {
                onStartDownload(deps.fullVersionName)
                return null
            }
        }
        downloader.downloadIncrementally(dir, deps.fullVersionName, callback)
        return downloadJob
    }


    fun vanillaDownloader() = MinecraftDownloaderBuilder.buildDefault()

    fun fabricDownloader() = MinecraftDownloaderBuilder.create().providerChain(
        DownloadProviderChain.create().addProvider(FabricDownloadProvider())
    ).build()
}
