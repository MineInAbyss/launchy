package com.mineinabyss.launchy.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.AwtWindow
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import com.mineinabyss.launchy.util.Dirs
import com.mineinabyss.launchy.util.OS
import java.awt.FileDialog
import java.awt.Frame
import java.io.FilenameFilter
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.isDirectory


@Composable
fun DirectoryDialog(
    shown: Boolean,
    title: String,
    fallbackTitle: String? = null,
    parent: Frame? = null,
    onCloseRequest: (result: Path?) -> Unit,
) {
    when {
        OS.get() == OS.WINDOWS || OS.get() == OS.MAC -> DirectoryPicker(
            shown,
            initialDirectory = Dirs.jdks.toString(),
            title = title,
            onFileSelected = { dir ->
                onCloseRequest(dir?.let { Path(it) })
            })

        shown -> AwtWindow(
            create = {
                object : FileDialog(parent, fallbackTitle ?: title, LOAD) {
                    override fun setVisible(value: Boolean) {
                        super.setVisible(value)
                        if (value) {
                            val path = files.firstOrNull()?.toPath()
                            if (path?.isDirectory() == true)
                                onCloseRequest(path)
                            else onCloseRequest(path?.parent)
                        }
                    }
                }
            },
            dispose = FileDialog::dispose
        )
    }
}

@Composable
fun SingleFileDialog(
    shown: Boolean,
    title: String,
    fallbackTitle: String? = null,
    parent: Frame? = null,
    onCloseRequest: (result: Path?) -> Unit,
    fileExtensions: () -> List<String>,
    fallbackFilter: FilenameFilter
) {
    when {
        OS.get() == OS.WINDOWS || OS.get() == OS.MAC -> FilePicker(
            shown,
            initialDirectory = Dirs.jdks.toString(),
            title = title,
            fileExtensions = fileExtensions(),
            onFileSelected = { file ->
                onCloseRequest(file?.let { Path(it.path) })
            })

        shown -> AwtWindow(
            create = {
                object : FileDialog(parent, fallbackTitle ?: title, LOAD) {
                    override fun setVisible(value: Boolean) {
                        super.setVisible(value)
                        if (value) {
                            onCloseRequest(files.firstOrNull()?.toPath())
                        }
                    }
                }.apply {
                    setFilenameFilter(fallbackFilter)
                }
            },
            dispose = FileDialog::dispose
        )
    }
}
