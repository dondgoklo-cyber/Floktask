package com.taskmanager.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Брендовые цвета (обратная совместимость с существующими импортами)
val Orange = Color(0xFFFF7A00)
val OrangeLight = Color(0xFFFFB74D)
val OrangeDark = Color(0xFFFF8C00)

/** Локальная композиция для доступа к расширенной палитре [AppColors] из любого компонента. */
val LocalAppColors = staticCompositionLocalOf { LightAppColors }

private val LightColors = lightColorScheme(
    primary = LightAppColors.primary,
    onPrimary = LightAppColors.onPrimary,
    primaryContainer = LightAppColors.primaryContainer,
    onPrimaryContainer = LightAppColors.onPrimaryContainer,
    secondary = LightAppColors.secondary,
    onSecondary = LightAppColors.onSecondary,
    secondaryContainer = LightAppColors.secondaryContainer,
    onSecondaryContainer = LightAppColors.onSecondaryContainer,
    tertiary = LightAppColors.warning,
    onTertiary = LightAppColors.onWarning,
    background = LightAppColors.background,
    onBackground = LightAppColors.onBackground,
    surface = LightAppColors.surface,
    onSurface = LightAppColors.onSurface,
    surfaceVariant = LightAppColors.surfaceVariant,
    onSurfaceVariant = LightAppColors.onSurfaceVariant,
    surfaceTint = LightAppColors.primary,
    outline = LightAppColors.outline,
    outlineVariant = LightAppColors.outlineVariant,
    error = LightAppColors.error,
    onError = LightAppColors.onError
)

private val DarkColors = darkColorScheme(
    primary = DarkAppColors.primary,
    onPrimary = DarkAppColors.onPrimary,
    primaryContainer = DarkAppColors.primaryContainer,
    onPrimaryContainer = DarkAppColors.onPrimaryContainer,
    secondary = DarkAppColors.secondary,
    onSecondary = DarkAppColors.onSecondary,
    secondaryContainer = DarkAppColors.secondaryContainer,
    onSecondaryContainer = DarkAppColors.onSecondaryContainer,
    tertiary = DarkAppColors.warning,
    onTertiary = DarkAppColors.onWarning,
    background = DarkAppColors.background,
    onBackground = DarkAppColors.onBackground,
    surface = DarkAppColors.surface,
    onSurface = DarkAppColors.onSurface,
    surfaceVariant = DarkAppColors.surfaceVariant,
    onSurfaceVariant = DarkAppColors.onSurfaceVariant,
    surfaceTint = DarkAppColors.primary,
    outline = DarkAppColors.outline,
    outlineVariant = DarkAppColors.outlineVariant,
    error = DarkAppColors.error,
    onError = DarkAppColors.onError
)

@Composable
fun TaskManagerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    val appColors = if (darkTheme) DarkAppColors else LightAppColors

    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = appTypography(),
            content = content
        )
    }
}

/** Удобный доступ к расширенной палитре из любого composable. */
object AppTheme {
    val colors: AppColors
        @Composable get() = LocalAppColors.current
}
