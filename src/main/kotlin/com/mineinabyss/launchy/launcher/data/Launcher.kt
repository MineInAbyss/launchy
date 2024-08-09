package com.mineinabyss.launchy.launcher.data

import com.mineinabyss.launchy.auth.data.ProfileRepository
import com.mineinabyss.launchy.core.ui.Dialog
import com.mineinabyss.launchy.core.ui.LaunchyUiState
import com.mineinabyss.launchy.core.ui.screens.dialog
import com.mineinabyss.launchy.instance.data.ModLoaderModel
import com.mineinabyss.launchy.instance.ui.GameInstanceState
import com.mineinabyss.launchy.util.AppDispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.to2mbn.jmccc.auth.AuthInfo
import org.to2mbn.jmccc.auth.Authenticator
import org.to2mbn.jmccc.launch.LauncherBuilder
import org.to2mbn.jmccc.launch.ProcessListener
import org.to2mbn.jmccc.mcdownloader.MinecraftDownloaderBuilder
import org.to2mbn.jmccc.mcdownloader.download.concurrent.CallbackAdapter
import org.to2mbn.jmccc.mcdownloader.download.concurrent.DownloadCallback
import org.to2mbn.jmccc.mcdownloader.download.tasks.DownloadTask
import org.to2mbn.jmccc.mcdownloader.provider.DownloadProviderChain
import org.to2mbn.jmccc.mcdownloader.provider.fabric.FabricDownloadProvider
import org.to2mbn.jmccc.option.JavaEnvironment
import org.to2mbn.jmccc.option.LaunchOption
import org.to2mbn.jmccc.option.MinecraftDirectory
import org.to2mbn.jmccc.version.Version
import java.nio.file.Path
import java.util.*
import kotlin.io.path.createParentDirectories
import kotlin.io.path.notExists


object Launcher {
    suspend fun launch(state: LaunchyUiState, pack: GameInstanceState, profile: ProfileRepository): Unit =
        coroutineScope {
        val dir = MinecraftDirectory(pack.instance.minecraftDir.toFile())
        val launcher = LauncherBuilder.buildDefault()
        val javaPath = state.jvm.javaPath
        if (javaPath == null || javaPath.notExists()) {
            dialog = Dialog.ChooseJVMPath
            return@coroutineScope
        }
        state.lastPlayed[pack.instance.config.name] = Date().time
        // Auth or show dialog
        when (val session = profile.currentSession) {
            null -> Authenticator.authOrShowDialog(state, profile) {
                launch { launch(state, pack, profile) }
            }

            else -> state.setProcessFor(pack.instance, launcher.launch(
                LaunchOption(
                    pack.modpack.modLoaders.fullVersionName,
                    Authenticator {
                        return@Authenticator AuthInfo(
                            session.mcProfile.name,
                            session.mcProfile.mcToken.accessToken,
                            session.mcProfile.id,
                            Collections.emptyMap(),
                            "msa",
                            "unknown",
                        )
                    },
                    dir
                ).apply {
                    maxMemory = state.jvm.memory
                    minMemory = state.jvm.memory
                    extraJvmArguments().clear()
                    extraJvmArguments().addAll(state.jvm.jvmArgs.split(" "))
                    javaEnvironment = JavaEnvironment(javaPath.toFile())
                },
                object : ProcessListener {
                    override fun onLog(p0: String?) {
                        System.out.println(p0)
                    }

                    override fun onErrorLog(p0: String?) {
                        System.err.println(p0)
                    }

                    override fun onExit(p0: Int) {
                        println("Exited with state $p0")

                        when (p0) {
                            255 -> dialog = Dialog.Error("Minecraft crashed!", "See logs for more info.")
                        }
                        state.setProcessFor(pack.instance, null)
                    }

                }
            ))
        }
    }

    fun download(
        modLoaders: ModLoaderModel,
        minecraftDir: Path,
        onStartDownload: (String) -> Unit = {},
        onFinishDownload: (String) -> Unit = {}
    ): Job {
        val downloadJob = Job()
        minecraftDir.createParentDirectories()
        val dir = MinecraftDirectory(minecraftDir.toFile())

        val downloader = when {
            modLoaders.fabricLoader != null -> fabricDownloader()
            else -> vanillaDownloader()
        }
        AppDispatchers.IO.launch {
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
                    onStartDownload(modLoaders.fullVersionName)
                    return null
                }
            }
            downloader.downloadIncrementally(dir, modLoaders.fullVersionName, callback)
        }
        return downloadJob
    }


    fun vanillaDownloader() = MinecraftDownloaderBuilder.buildDefault()

    fun fabricDownloader() = MinecraftDownloaderBuilder.create().providerChain(
        DownloadProviderChain.create().addProvider(FabricDownloadProvider())
    ).build()
}
