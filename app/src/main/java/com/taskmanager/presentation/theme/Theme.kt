package com.taskmanager.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Яркий оранжевый — основной бренд-цвет
val Orange = Color(0xFFFF6D00)
val OrangeLight = Color(0xFFFFAB40)
val OrangeDark = Color(0xFFFF9100)

// Светлая тема — чисто белый
private val LightColors = lightColorScheme(
    primary = Orange,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFF3E0),
    onPrimaryContainer = Color(0xFF3E2700),
    secondary = Color(0xFF00897B),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB2DFDB),
    onSecondaryContainer = Color(0xFF003734),
    tertiary = Color(0xFFFFB300),
    onTertiary = Color(0xFF3E2700),
    background = Color.White,
    onBackground = Color(0xFF1A1A1A),
    surface = Color.White,
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFF616161),
    outline = Color(0xFFBDBDBD),
    error = Color(0xFFD32F2F),
    onError = Color.White,
)

// Тёмная тема — глубокий чёрный + ярко-оранжевый
private val DarkColors = darkColorScheme(
    primary = OrangeDark,
    onPrimary = Color(0xFF1A0A00),
    primaryContainer = Color(0xFF4E2C00),
    onPrimaryContainer = Color(0xFFFFD9B0),
    secondary = Color(0xFF80CBC4),
    onSecondary = Color(0xFF003734),
    secondaryContainer = Color(0xFF004D40),
    onSecondaryContainer = Color(0xFFB2DFDB),
    tertiary = Color(0xFFFFD54F),
    onTertiary = Color(0xFF3E2700),
    background = Color(0xFF000000),
    onBackground = Color(0xFFE6E6E6),
    surface = Color(0xFF0A0A0A),
    onSurface = Color(0xFFE6E6E6),
    surfaceVariant = Color(0xFF1A1A1A),
    onSurfaceVariant = Color(0xFF9E9E9E),
    outline = Color(0xFF424242),
    error = Color(0xFFFF5252),
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
