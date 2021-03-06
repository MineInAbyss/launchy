package com.mineinabyss.launchy

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.memberProperties

@Composable
fun rememberMIAColorScheme(): ColorScheme = remember {
    darkColorScheme().also { scheme ->
        ColorScheme::class.memberProperties.filterIsInstance<KMutableProperty1<ColorScheme, Color>>()
            .filter { "error" !in it.name.toLowerCase() }
            .map { prop ->
                val col = (prop.get(scheme))
                val hsbVals = FloatArray(3)
                val javaCol = java.awt.Color(col.red, col.green, col.blue, col.alpha)
                java.awt.Color.RGBtoHSB(javaCol.red, javaCol.green, javaCol.blue, hsbVals)
                val shiftedColor = Color(java.awt.Color.HSBtoRGB(0.02f, hsbVals[1], hsbVals[2]))
                prop.set(
                    scheme,
                    col.copy(red = shiftedColor.red, blue = shiftedColor.blue, green = shiftedColor.green)
                )
            }
    }
}
