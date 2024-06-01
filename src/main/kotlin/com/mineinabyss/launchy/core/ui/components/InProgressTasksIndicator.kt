package com.mineinabyss.launchy.core.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.core.data.TasksRepository
import com.mineinabyss.launchy.core.ui.screens.Screen
import com.mineinabyss.launchy.core.ui.screens.screen
import com.mineinabyss.launchy.instance.ui.components.SlightBackgroundTint
import com.mineinabyss.launchy.instance.ui.components.settings.infobar.InfoBarProperties
import com.mineinabyss.launchy.util.InProgressTask
import org.koin.compose.koinInject

@Composable
fun InProgressTasksIndicator(
    tasks: TasksRepository = koinInject()
) {
    val progressBarHeight by animateDpAsState(if (screen == Screen.InstanceSettings) InfoBarProperties.height else 0.dp)
    val inProgress by tasks.inProgress.collectAsState()

    if (inProgress.isNotEmpty()) Box(Modifier.fillMaxSize().padding(bottom = progressBarHeight)) {
        val task = inProgress.values.first()
        val textModifier = Modifier.align(Alignment.BottomStart).padding(start = 10.dp, bottom = 20.dp)
        val progressBarModifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter)
        val progressBarColor = MaterialTheme.colorScheme.primaryContainer
        SlightBackgroundTint(Modifier.height(50.dp))
        when (task) {
            is InProgressTask.WithPercentage -> {
                Text(
                    "${task.name}... (${task.current}/${task.total}${if (task.measurement != null) " ${task.measurement}" else ""})",
                    modifier = textModifier
                )
                LinearProgressIndicator(
                    progress = task.current.toFloat() / task.total,
                    modifier = progressBarModifier,
                    color = progressBarColor
                )
            }

            else -> {
                Text(
                    "${task.name}...",
                    modifier = textModifier
                )

                LinearProgressIndicator(
                    modifier = progressBarModifier,
                    color = progressBarColor
                )
            }
        }
    }
}
