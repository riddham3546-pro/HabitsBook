package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val HabitLightColorScheme = lightColorScheme(
    primary = SkyBluePrimary,
    onPrimary = Color.White,
    primaryContainer = LightBluePillBg,
    onPrimaryContainer = SkyBluePrimary,
    secondary = SkyBlueAccent,
    onSecondary = Color.White,
    secondaryContainer = SurfaceOffWhite,
    onSecondaryContainer = TextDarkPrimary,
    tertiary = HabitBloomGreen,
    onTertiary = Color.White,
    background = WhiteBackground,
    onBackground = TextDarkPrimary,
    surface = SurfacePureWhite,
    onSurface = TextDarkPrimary,
    surfaceVariant = SurfaceOffWhite,
    onSurfaceVariant = TextDarkSecondary,
    outline = SurfaceBorderLight,
    outlineVariant = SurfaceBorderSubtle
)

@Composable
fun HabitBloomTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = HabitLightColorScheme,
        typography = Typography,
        content = content
    )
}
