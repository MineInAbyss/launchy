package com.mineinabyss.launchy.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TitleSmall(text: String) {
    Box(Modifier.padding(top = 12.dp, bottom = 8.dp)) {
        Text(text, style = MaterialTheme.typography.titleSmall)
    }
}

@Composable
fun TitleMedium(text: String) {
    Box(Modifier.padding(top = 12.dp, bottom = 8.dp)) {
        Text(text, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun TitleLarge(text: String) {
    Box(Modifier.padding(top = 8.dp, bottom = 4.dp)) {
        Text(text, style = MaterialTheme.typography.titleLarge)
    }
}


@Composable
fun Setting(title: String, icon: @Composable () -> Unit = {}, content: @Composable () -> Unit) {
    Column {
        TitleSmall(title)
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            icon()
            Column {
                content()
            }
        }
    }
}
