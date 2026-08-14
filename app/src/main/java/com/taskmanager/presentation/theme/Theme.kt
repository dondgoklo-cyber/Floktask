package com.taskmanager.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Orange palette
val OrangePrimary = Color(0xFFF57C00)
val OrangePrimaryDark = Color(0xFFFF9800)
val OrangeContainer = Color(0xFFFFE0B2)
val OrangeOnContainer = Color(0xFF3E2A00)

val TealSecondary = Color(0xFF00897B)
val TealSecondaryDark = Color(0xFF80CBC4)

val AmberTertiary = Color(0xFFFFB300)
val AmberTertiaryDark = Color(0xFFFFD54F)

val SurfaceLight = Color(0xFFFFFBFE)
val SurfaceDark = Color(0xFF1C1B1F)
val BackgroundLight = Color(0xFFFAF6F0)
val BackgroundDark = Color(0xFF121212)

private val LightColors = lightColorScheme(
    primary = OrangePrimary,
    onPrimary = Color.White,
    primaryContainer = OrangeContainer,
    onPrimaryContainer = OrangeOnContainer,
    secondary = TealSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB2DFDB),
    onSecondaryContainer = Color(0xFF003734),
    tertiary = AmberTertiary,
    onTertiary = Color(0xFF3E2A00),
    tertiaryContainer = Color(0xFFFFE082),
    onTertiaryContainer = Color(0xFF3E2A00),
    background = BackgroundLight,
    onBackground = Color(0xFF1C1B1F),
    surface = SurfaceLight,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFF3E8DC),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFFBDBDBD),
    error = Color(0xFFD32F2F),
    onError = Color.White,
)

private val DarkColors = darkColorScheme(
    primary = OrangePrimaryDark,
    onPrimary = Color(0xFF3E2A00),
    primaryContainer = Color(0xFFBF5F00),
    onPrimaryContainer = OrangeContainer,
    secondary = TealSecondaryDark,
    onSecondary = Color(0xFF003734),
    secondaryContainer = Color(0xFF004D40),
    onSecondaryContainer = Color(0xFFB2DFDB),
    tertiary = AmberTertiaryDark,
    onTertiary = Color(0xFF3E2A00),
    tertiaryContainer = Color(0xFF8C6D00),
    onTertiaryContainer = Color(0xFFFFE082),
    background = BackgroundDark,
    onBackground = Color(0xFFE6E1E5),
    surface = SurfaceDark,
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF2C2A2E),
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = Color(0xFF6E6E6E),
    error = Color(0xFFEF5350),
    onError = Color(0xFF410E0B),
)

@Composable
fun TaskManagerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}
