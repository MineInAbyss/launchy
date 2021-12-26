package com.mineinabyss.launchy.ui.screens

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.LocalConfig
import com.mineinabyss.launchy.LocalVersions
import com.mineinabyss.launchy.data.Dirs
import com.mineinabyss.launchy.data.Mod
import com.mineinabyss.launchy.logic.Downloader
import com.mineinabyss.launchy.ui.ModGroup
import com.mineinabyss.launchy.util.Option
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.awt.FileDialog
import kotlin.io.path.div

@Composable
@Preview
fun MainScreen() {
    val config = LocalConfig
    val versions = LocalVersions
    Scaffold(
        topBar = {
            Row {
                var path by remember { mutableStateOf("") }
                Button(onClick = {
                    path = FileDialog(ComposeWindow()).apply {
                        setFilenameFilter { dir, name -> name.endsWith(".minecraft") }
                        isVisible = true
                    }.directory
                }) {
                    Text("File Picker")
                }
                Text(path)

                val coroutineScope = rememberCoroutineScope()
                var downloaded by remember { mutableStateOf(0) }
                var total by remember { mutableStateOf(0) }
                var isDownloading by remember { mutableStateOf(false) }

                Button(enabled = !isDownloading, onClick = {
                    isDownloading = true
                    val downloadMods: Set<Mod> = config
                        // Add all mods from enabled groups
                        .groups
                        .asSequence()
                        .filter { it.value == Option.ENABLED }
                        .mapNotNull { versions.modGroups[it.key] }
                        .flatten()
                        // Add individually enabled mods
                        .plus(config.enabledMods.mapNotNull { name -> versions.mods[name] })
                        // Remove explicitly disabled groups
                        .minus(
                            config.groups
                                .filter { it.value == Option.DISABLED }
                                .mapNotNull { versions.modGroups[it.key] }
                                .flatten()
                                .toSet()
                        )
                        // Ignore identical URLs
                        .filter { config.downloads[it.name] != it.url }
                        .toSet()
                    total = downloadMods.count()

                    for (mod in downloadMods)
                        coroutineScope.launch(Dispatchers.IO) {
                            Downloader.download(
                                url = mod.url,
                                writeTo = Dirs.mods / "${mod.name}.jar"
                            )
                            config.downloads[mod.name] = mod.url
                            downloaded++
                        }
                }) {
                    Text("Download mods")
                }
                if(downloaded == total) {
                    isDownloading = false
                    total = 0
                }
                Text("$downloaded / $total")
            }
        }
    ) {
        Box {
            val lazyListState = rememberLazyListState()
            LazyColumn(Modifier.fillMaxSize().padding(end = 12.dp), lazyListState) {
                items(versions.modGroups.toList()) { (group, mods) ->
                    ModGroup(group, mods)
                }
            }
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(lazyListState)
            )
        }
    }
}
