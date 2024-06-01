package com.mineinabyss.launchy.instance.data

//@OptIn(ExperimentalCoroutinesApi::class)
//class GameInstanceDataSource(
//    val configDir: Path,
//    val config: InstanceConfig,
//) {
//    suspend fun createModpackState(state: LaunchyUiState, awaitUpdatesCheck: Boolean = false): GameInstanceState? {
//        val userConfig = InstanceUserConfig.load(userConfigFile).getOrNull() ?: InstanceUserConfig()
//
//        val modpack = state.runTask("loadingModpack ${config.name}", InProgressTask("Loading modpack ${config.name}")) {
//            config.source.loadInstance(this)
//                .showDialogOnError("Failed to read instance")
//                .getOrElse {
//                    it.printStackTrace()
//                    return null
//                }
//        }
//        val cloudUrl = config.cloudInstanceURL
//        if (cloudUrl != null) {
//            AppDispatchers.IO.launch {
//                val result = Downloader.checkUpdates(this@GameInstanceDataSource, cloudUrl)
//                if (result !is UpdateResult.UpToDate) updatesAvailable = true
//            }.also { if (awaitUpdatesCheck) it.join() }
//        }
//        return GameInstanceState(this, modpack, userConfig)
//    }
//
//    init {
//        require(configDir.isDirectory()) { "Game instance at $configDir must be a directory" }
//        userMods
//    }
//
//    companion object {
//        fun createCloudInstance(state: LaunchyUiState, cloud: CloudInstanceWithHeaders) {
//            val instanceDir = Dirs.modpackConfigDir(cloud.config.name)
//            instanceDir.createDirectories()
//
//            Formats.yaml.encodeToStream(
//                cloud.config.copy(cloudInstanceURL = cloud.url),
//                (instanceDir / "instance.yml").outputStream()
//            )
//            val instance = GameInstanceDataSource(instanceDir)
//            Downloader.saveHeaders(instance, cloud.url, cloud.headers)
//            state.gameInstances += instance
//        }
//    }
//
//    private suspend fun loadBackground() {
//        runCatching {
//            Downloader.download(config.backgroundURL, config.backgroundPath, Downloader.Options(overwrite = false))
//            val painter = BitmapPainter(loadImageBitmap(config.backgroundPath.inputStream()))
//            cachedBackground = painter
//        }.onFailure { it.printStackTrace() }
//    }
//
//    private suspend fun loadLogo() {
//        runCatching {
//            Downloader.download(config.logoURL, config.logoPath, Downloader.Options(overwrite = false))
//            val painter = BitmapPainter(loadImageBitmap(config.logoPath.inputStream()))
//            cachedLogo = painter
//        }.onFailure { it.printStackTrace() }
//    }
//
//    private var cachedBackground: BitmapPainter? = null
//    private var cachedLogo: BitmapPainter? = null
//
//    suspend fun getBackground() = withContext(imageLoaderDispatcher) {
//        if (cachedBackground == null) loadLogo()
//        cachedBackground
//    }
//
//    suspend fun getLogo(): BitmapPainter? = withContext(imageLoaderDispatcher) {
//        if (cachedLogo == null) loadLogo()
//        cachedLogo
//    }
//}
