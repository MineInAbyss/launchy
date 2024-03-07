package com.mineinabyss.launchy.ui.elements

import androidx.compose.foundation.layout.Box
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
