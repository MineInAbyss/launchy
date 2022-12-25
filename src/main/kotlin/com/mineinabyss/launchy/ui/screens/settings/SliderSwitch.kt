package com.mineinabyss.launchy.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlin.math.roundToLong

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SliderSwitch(
    label: String = "",
    modifier: Modifier = Modifier,
    valueRange: IntRange = 1..16,
): Float {
    var sliderPosition by remember { mutableStateOf(2f) }
    var sliderPos by remember { mutableStateOf(sliderPosition.toString()) }

    Text(label, color = MaterialTheme.colorScheme.error)
    TextField(
        label = sliderPosition.toString(),
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Left),
        modifier = modifier.width(100.dp),
        onValueChange = {
            println("$sliderPos $sliderPosition")
            sliderPosition = sliderPos.toFloatOrNull() ?: sliderPosition
        }
    )
    // This is called too often, and should only be called when textfield triggers onValueChange but cant? not sure
    //sliderPosition = sliderPos.toFloatOrNull() ?: sliderPosition

    Slider(
        value = sliderPosition,
        colors = SliderDefaults.colors(
            thumbColor = MaterialTheme.colorScheme.error, Color.Transparent,
            activeTrackColor = MaterialTheme.colorScheme.errorContainer, Color.Transparent
        ),
        onValueChangeFinished = {
            sliderPos = sliderPosition.toString()
        },
        valueRange = valueRange.first.toFloat()..valueRange.last.toFloat(),
        steps = valueRange.last - valueRange.first,
        onValueChange = {
            sliderPosition = it.roundToLong().toFloat()
        },
        modifier = modifier
    )
    return sliderPosition
}
