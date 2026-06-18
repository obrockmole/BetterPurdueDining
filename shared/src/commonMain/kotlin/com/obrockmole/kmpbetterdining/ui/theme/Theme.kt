package com.obrockmole.kmpbetterdining.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PurdueGold,
    secondary = PurdueGray,
    tertiary = PurdueLightGray
)

private val LightColorScheme = lightColorScheme(
    primary = PurdueGold,
    secondary = PurdueGray,
    tertiary = PurdueLightGray
)

private fun randomColor(): Color {
    return Color(
        red = (25..250).random(),
        green = (25..250).random(),
        blue = (25..250).random()
    )
}

private fun rainbowColorScheme(): ColorScheme {
    return darkColorScheme(
        primary = randomColor(),
        onPrimary = randomColor(),
        primaryContainer = randomColor(),
        onPrimaryContainer = randomColor(),
        inversePrimary = randomColor(),
        secondary = randomColor(),
        onSecondary = randomColor(),
        secondaryContainer = randomColor(),
        onSecondaryContainer = randomColor(),
        tertiary = randomColor(),
        onTertiary = randomColor(),
        tertiaryContainer = randomColor(),
        onTertiaryContainer = randomColor(),
        background = randomColor(),
        onBackground = randomColor(),
        surface = randomColor(),
        onSurface = randomColor(),
        surfaceVariant = randomColor(),
        onSurfaceVariant = randomColor(),
        surfaceTint = randomColor(),
        inverseSurface = randomColor(),
        inverseOnSurface = randomColor(),
        error = randomColor(),
        onError = randomColor(),
        errorContainer = randomColor(),
        onErrorContainer = randomColor(),
        outline = randomColor(),
        outlineVariant = randomColor(),
        scrim = randomColor(),
        surfaceBright = randomColor(),
        surfaceContainer = randomColor(),
        surfaceContainerHigh = randomColor(),
        surfaceContainerHighest = randomColor(),
        surfaceContainerLow = randomColor(),
        surfaceContainerLowest = randomColor(),
        surfaceDim = randomColor(),
        primaryFixed = randomColor(),
        primaryFixedDim = randomColor(),
        onPrimaryFixed = randomColor(),
        onPrimaryFixedVariant = randomColor(),
        secondaryFixed = randomColor(),
        secondaryFixedDim = randomColor(),
        onSecondaryFixed = randomColor(),
        onSecondaryFixedVariant = randomColor(),
        tertiaryFixed = randomColor(),
        tertiaryFixedDim = randomColor(),
        onTertiaryFixed = randomColor(),
        onTertiaryFixedVariant = randomColor()
    )
}

@Composable
fun BetterPurdueDiningTheme(
    theme: String = "Material",
    content: @Composable () -> Unit
) {
    val colorScheme = remember(theme) {
        when (theme) {
            "Light" -> LightColorScheme
            "Dark" -> DarkColorScheme
            "Rainbow" -> rainbowColorScheme()
            else -> DarkColorScheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = getTypography(),
        content = content
    )
}