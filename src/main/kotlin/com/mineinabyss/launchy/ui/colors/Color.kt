package com.mineinabyss.launchy.ui.colors

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

val md_theme_light_primary = Color(0xFF924C00)
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = Color(0xFFFFDCC4)
val md_theme_light_onPrimaryContainer = Color(0xFF2F1400)
val md_theme_light_secondary = Color(0xFF745945)
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = Color(0xFFFFDCC4)
val md_theme_light_onSecondaryContainer = Color(0xFF2A1707)
val md_theme_light_tertiary = Color(0xFF5D6136)
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_tertiaryContainer = Color(0xFFE3E7AF)
val md_theme_light_onTertiaryContainer = Color(0xFF1A1D00)
val md_theme_light_error = Color(0xFFBA1A1A)
val md_theme_light_errorContainer = Color(0xFFFFDAD6)
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_onErrorContainer = Color(0xFF410002)
val md_theme_light_background = Color(0xFFFFFBFF)
val md_theme_light_onBackground = Color(0xFF201A17)
val md_theme_light_surface = Color(0xFFFFFBFF)
val md_theme_light_onSurface = Color(0xFF201A17)
val md_theme_light_surfaceVariant = Color(0xFFF3DFD2)
val md_theme_light_onSurfaceVariant = Color(0xFF52443B)
val md_theme_light_outline = Color(0xFF84746A)
val md_theme_light_inverseOnSurface = Color(0xFFFBEEE8)
val md_theme_light_inverseSurface = Color(0xFF362F2B)
val md_theme_light_inversePrimary = Color(0xFFFFB781)
val md_theme_light_shadow = Color(0xFF000000)
val md_theme_light_surfaceTint = Color(0xFF924C00)
val md_theme_light_outlineVariant = Color(0xFFD6C3B7)
val md_theme_light_scrim = Color(0xFF000000)

class LaunchyColors(val hue: Float = 0f) {
    fun Color.applyHue(): Color {
        val hsbVals = FloatArray(3)
        val javaCol = java.awt.Color(red, green, blue, alpha)
        java.awt.Color.RGBtoHSB(javaCol.red, javaCol.green, javaCol.blue, hsbVals)
        val shiftedColor = Color(java.awt.Color.HSBtoRGB(hue, hsbVals[1], hsbVals[2]))
        return copy(red = shiftedColor.red, blue = shiftedColor.blue, green = shiftedColor.green)
    }
    val md_theme_dark_primary = Color(0xFFD0BCFF).applyHue()
    val md_theme_dark_onPrimary = Color(0xFF381E72).applyHue()
    val md_theme_dark_primaryContainer = Color(0xFF4F378B).applyHue()
    val md_theme_dark_onPrimaryContainer = Color(0xFFEADDFF).applyHue()
    val md_theme_dark_secondary = Color(0xFFCCC2DC).applyHue()
    val md_theme_dark_onSecondary = Color(0xFF332D41).applyHue()
    val md_theme_dark_secondaryContainer = Color(0xFF4A4458).applyHue()
    val md_theme_dark_onSecondaryContainer = Color(0xFFE8DEF8).applyHue()
    val md_theme_dark_tertiary = Color(0xFFEFB8C8).applyHue()
    val md_theme_dark_onTertiary = Color(0xFF492532).applyHue()
    val md_theme_dark_tertiaryContainer = Color(0xFF633B48).applyHue()
    val md_theme_dark_onTertiaryContainer = Color(0xFFFFD8E4).applyHue()
    val md_theme_dark_error = Color(0xffed766f)
    val md_theme_dark_onError = Color(0xFF601410)
    val md_theme_dark_errorContainer = Color(0xFF8C1D18)
    val md_theme_dark_onErrorContainer = Color(0xFFF9DEDC)
    val md_theme_dark_outline = Color(0xFF938F99).applyHue()
    val md_theme_dark_background = Color(0xFF1C1B1F).applyHue()
    val md_theme_dark_onBackground = Color(0xFFE6E1E5).applyHue()
    val md_theme_dark_surface = Color(0xFF1C1B1F).applyHue()
    val md_theme_dark_onSurface = Color(0xFFE6E1E5).applyHue()
    val md_theme_dark_surfaceVariant = Color(0xFF49454F).applyHue()
    val md_theme_dark_onSurfaceVariant = Color(0xFFCAC4D0).applyHue()
    val md_theme_dark_inverseSurface = Color(0xFFE6E1E5).applyHue()
    val md_theme_dark_inverseOnSurface = Color(0xFF313033).applyHue()
    val md_theme_dark_inversePrimary = Color(0xFF6750A4).applyHue()
    val md_theme_dark_shadow = Color(0xFF000000).applyHue()
    val md_theme_dark_surfaceTint = Color(0xFFD0BCFF).applyHue()
    val md_theme_dark_outlineVariant = Color(0xFF49454F).applyHue()
    val md_theme_dark_scrim = Color(0xFF000000).applyHue()


    val DarkColors = darkColorScheme(
        primary = md_theme_dark_primary,
        onPrimary = md_theme_dark_onPrimary,
        primaryContainer = md_theme_dark_primaryContainer,
        onPrimaryContainer = md_theme_dark_onPrimaryContainer,
        secondary = md_theme_dark_secondary,
        onSecondary = md_theme_dark_onSecondary,
        secondaryContainer = md_theme_dark_secondaryContainer,
        onSecondaryContainer = md_theme_dark_onSecondaryContainer,
        tertiary = md_theme_dark_tertiary,
        onTertiary = md_theme_dark_onTertiary,
        tertiaryContainer = md_theme_dark_tertiaryContainer,
        onTertiaryContainer = md_theme_dark_onTertiaryContainer,
        error = md_theme_dark_error,
        errorContainer = md_theme_dark_errorContainer,
        onError = md_theme_dark_onError,
        onErrorContainer = md_theme_dark_onErrorContainer,
        background = md_theme_dark_background,
        onBackground = md_theme_dark_onBackground,
        surface = md_theme_dark_surface,
        onSurface = md_theme_dark_onSurface,
        surfaceVariant = md_theme_dark_surfaceVariant,
        onSurfaceVariant = md_theme_dark_onSurfaceVariant,
        outline = md_theme_dark_outline,
        inverseOnSurface = md_theme_dark_inverseOnSurface,
        inverseSurface = md_theme_dark_inverseSurface,
        inversePrimary = md_theme_dark_inversePrimary,
        surfaceTint = md_theme_dark_surfaceTint,
        outlineVariant = md_theme_dark_outlineVariant,
        scrim = md_theme_dark_scrim,
    )
}


val seed = Color(0xFF934C00)
