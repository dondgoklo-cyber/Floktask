package com.taskmanager.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Централизованная система design tokens.
 * Все цвета, отступы, радиусы и типографика определены здесь и переиспользуются во всём приложении.
 * Hardcoded цвета в отдельных компонентах запрещены — используйте [AppColors] / [LocalAppColors].
 */

// ───────────────────────── Spacing ─────────────────────────

object Spacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 24.dp
    val xxl = 32.dp
    val xxxl = 48.dp

    /** Минимальный touch target (accessibility: 48dp). */
    val touchTarget = 48.dp
}

// ───────────────────────── Radius ─────────────────────────

object Radius {
    val xs = 6.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 20.dp
    val xxl = 28.dp
    val full = 9999.dp
}

// ───────────────────────── Elevation ─────────────────────────

object Elevation {
    val none = 0.dp
    val xs = 1.dp
    val sm = 2.dp
    val md = 4.dp
    val lg = 8.dp
    val xl = 12.dp
}

// ───────────────────────── AppColors ─────────────────────────
// Semantic color palette. Light и Dark варианты согласованы между всеми экранами.

data class AppColors(
    // Brand
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val secondary: Color,
    val onSecondary: Color,
    val secondaryContainer: Color,
    val onSecondaryContainer: Color,

    // Surfaces
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val onSurface: Color,
    val surfaceVariant: Color,
    val onSurfaceVariant: Color,
    val surfaceElevated: Color,
    val surfaceElevatedHigh: Color,

    // Borders & dividers
    val border: Color,
    val borderStrong: Color,
    val divider: Color,

    // Semantic
    val success: Color,
    val onSuccess: Color,
    val warning: Color,
    val onWarning: Color,
    val danger: Color,
    val onDanger: Color,
    val info: Color,
    val onInfo: Color,

    // Outline
    val outline: Color,
    val outlineVariant: Color,
    val error: Color,
    val onError: Color
)

object LightAppColors : AppColors(
    primary = Color(0xFFFF6D00),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFE0B2),
    onPrimaryContainer = Color(0xFF3E2700),
    secondary = Color(0xFF00897B),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB2DFDB),
    onSecondaryContainer = Color(0xFF003734),

    background = Color.White,
    onBackground = Color(0xFF1A1A1A),
    surface = Color.White,
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFF616161),
    surfaceElevated = Color(0xFFFAFAFA),
    surfaceElevatedHigh = Color(0xFFF0F0F0),

    border = Color(0xFFE0E0E0),
    borderStrong = Color(0xFFBDBDBD),
    divider = Color(0xFFEEEEEE),

    success = Color(0xFF2E7D32),
    onSuccess = Color.White,
    warning = Color(0xFFED6C02),
    onWarning = Color.White,
    danger = Color(0xFFC62828),
    onDanger = Color.White,
    info = Color(0xFF0277BD),
    onInfo = Color.White,

    outline = Color(0xFF9E9E9E),
    outlineVariant = Color(0xFFE0E0E0),
    error = Color(0xFFC62828),
    onError = Color.White
)

object DarkAppColors : AppColors(
    primary = Color(0xFFFF9100),
    onPrimary = Color(0xFF1A0A00),
    primaryContainer = Color(0xFF4E2C00),
    onPrimaryContainer = Color(0xFFFFD9B0),
    secondary = Color(0xFF80CBC4),
    onSecondary = Color(0xFF003734),
    secondaryContainer = Color(0xFF004D40),
    onSecondaryContainer = Color(0xFFB2DFDB),

    background = Color(0xFF000000),
    onBackground = Color(0xFFECECEC),
    surface = Color(0xFF0A0A0A),
    onSurface = Color(0xFFECECEC),
    surfaceVariant = Color(0xFF1A1A1A),
    onSurfaceVariant = Color(0xFF9E9E9E),
    surfaceElevated = Color(0xFF121212),
    surfaceElevatedHigh = Color(0xFF1E1E1E),

    border = Color(0xFF2A2A2A),
    borderStrong = Color(0xFF424242),
    divider = Color(0xFF1E1E1E),

    success = Color(0xFF66BB6A),
    onSuccess = Color(0xFF003A00),
    warning = Color(0xFFFFA726),
    onWarning = Color(0xFF332100),
    danger = Color(0xFFEF5350),
    onDanger = Color(0xFF3B0000),
    info = Color(0xFF4FC3F7),
    onInfo = Color(0xFF002130),

    outline = Color(0xFF616161),
    outlineVariant = Color(0xFF2A2A2A),
    error = Color(0xFFFF5252),
    onError = Color(0xFF410E0B)
)

// ───────────────────────── Typography ─────────────────────────

object AppTypography {
    val displayLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 40.sp, lineHeight = 48.sp)
    val displayMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 32.sp, lineHeight = 40.sp)
    val displaySmall = TextStyle(fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 36.sp)

    val headlineMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 24.sp, lineHeight = 32.sp)
    val headlineSmall = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 28.sp)

    val titleLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 18.sp, lineHeight = 24.sp)
    val titleMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 22.sp)
    val titleSmall = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp)

    val bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp)
    val bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp)
    val bodySmall = TextStyle(fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 16.sp)

    val labelLarge = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp)
    val labelMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp)
    val labelSmall = TextStyle(fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 16.sp)
}

/** Material3 Typography, собранный из [AppTypography] для консистентности. */
fun appTypography(): Typography = Typography(
    displayLarge = AppTypography.displayLarge,
    displayMedium = AppTypography.displayMedium,
    displaySmall = AppTypography.displaySmall,
    headlineMedium = AppTypography.headlineMedium,
    headlineSmall = AppTypography.headlineSmall,
    titleLarge = AppTypography.titleLarge,
    titleMedium = AppTypography.titleMedium,
    titleSmall = AppTypography.titleSmall,
    bodyLarge = AppTypography.bodyLarge,
    bodyMedium = AppTypography.bodyMedium,
    bodySmall = AppTypography.bodySmall,
    labelLarge = AppTypography.labelLarge,
    labelMedium = AppTypography.labelMedium,
    labelSmall = AppTypography.labelSmall
)
