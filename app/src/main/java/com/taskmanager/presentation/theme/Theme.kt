package com.taskmanager.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF6200EE),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE9DDFF),
    onPrimaryContainer = Color(0xFF1E0054),
    secondary = Color(0xFF00897B),
    onSecondary = Color.White,
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFBB86FC),
    onPrimary = Color(0xFF3700B3),
    primaryContainer = Color(0xFF4B00B4),
    onPrimaryContainer = Color(0xFFE9DDFF),
    secondary = Color(0xFF80CBC4),
    onSecondary = Color(0xFF003734),
    background = Color(0xFF1C1B1F),
    surface = Color(0xFF1C1B1F),
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
