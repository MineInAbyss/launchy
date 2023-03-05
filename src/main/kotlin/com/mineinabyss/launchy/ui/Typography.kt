package com.mineinabyss.launchy.ui

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font


val NotoSans = FontFamily(
    Font("font/NotoSans-Regular.ttf", FontWeight.Normal),
    Font("font/NotoSans-Medium.ttf", FontWeight.Medium),
    Font("font/NotoSans-Bold.ttf", FontWeight.Bold),
)

val LaunchyTypography: Typography
    get() {
        val material = Typography()
        return Typography(
            displayLarge = material.displayLarge.copy(fontFamily = NotoSans),
            displayMedium = material.displayMedium.copy(fontFamily = NotoSans),
            displaySmall = material.displaySmall.copy(fontFamily = NotoSans),
            headlineLarge = material.headlineLarge.copy(fontFamily = NotoSans),
            headlineMedium = material.headlineMedium.copy(fontFamily = NotoSans),
            headlineSmall = material.headlineSmall.copy(fontFamily = NotoSans),
            titleLarge = material.titleLarge.copy(fontFamily = NotoSans),
            titleMedium = material.titleMedium.copy(fontFamily = NotoSans),
            titleSmall = material.titleSmall.copy(fontFamily = NotoSans),
            bodyLarge = material.bodyLarge.copy(fontFamily = NotoSans),
            bodyMedium = material.bodyMedium.copy(fontFamily = NotoSans),
            bodySmall = material.bodySmall.copy(fontFamily = NotoSans),
            labelLarge = material.labelLarge.copy(fontFamily = NotoSans),
            labelMedium = material.labelMedium.copy(fontFamily = NotoSans),
            labelSmall = material.labelSmall.copy(fontFamily = NotoSans),
        )
    }


