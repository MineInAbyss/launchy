package com.mineinabyss.launchy.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.LocalLaunchyState
import kotlinx.serialization.Serializable
import kotlin.math.roundToInt

@Composable
fun JavaGroup() {
    var expanded by remember { mutableStateOf(false) }
    val arrowRotationState by animateFloatAsState(targetValue = if (expanded) 180f else 0f)
    val state = LocalLaunchyState

    Surface(
        tonalElevation = 1.dp,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.padding(2.dp).fillMaxWidth().clickable { expanded = !expanded },
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.height(40.dp)
            ) {
                Spacer(Modifier.width(10.dp))
                Text("Settings", Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge,)
                Spacer(Modifier.width(10.dp))
                Icon(Icons.Rounded.ArrowDropDown, "Show Java Settings", Modifier.rotate(arrowRotationState))
                Spacer(Modifier.width(10.dp))
            }
            AnimatedVisibility(expanded) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.height(40.dp)
                    ) {
                        Spacer(Modifier.width(10.dp))
                        val minRam = SliderSwitch(label = "Minimum RAM", valueRange = 1..12).roundToInt()
                        val maxRam = SliderSwitch(label = "Maximum RAM:", valueRange = 1..12).roundToInt()
                        // Figure out way to handle this, probably storing via state or something
                        state.clientSettings = ClientSettings(state.clientSettings.minecraft, JavaSettings(minRam, maxRam))
                    }
                }
            }
        }
    }
}

@Serializable
data class ClientSettings(val minecraft: MinecraftSettings, val java: JavaSettings)
@Serializable
data class MinecraftSettings(val resWidth: Int = 1280, val resHeight: Int = 720, val fullscreenLaunch: Boolean = false)
@Serializable
data class JavaSettings(val minRamAmount: Int = 2, val maxRamAmount: Int = 2)
