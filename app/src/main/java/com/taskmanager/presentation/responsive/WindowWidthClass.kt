package com.taskmanager.presentation.responsive

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Material-like window width breakpoints for responsive layouts (issue 33:
 * UI was phone-only; on tablet there's empty space). Used to switch between
 * single-pane (phone) and two-pane (tablet) navigation.
 */
@Stable
enum class WindowWidthClass {
    COMPACT,   // < 600 dp  — phone, single pane
    MEDIUM,    // 600–840 dp — small tablet, foldable
    EXPANDED;  // > 840 dp — tablet, two-pane / multi-pane

    val isTablet: Boolean get() = this != COMPACT
    val supportsTwoPane: Boolean get() = this == EXPANDED
}

/**
 * Resolves the current [WindowWidthClass] from the window width in pixels.
 */
@Composable
fun currentWindowWidthClass(): WindowWidthClass {
    val density = LocalDensity.current
    val widthPx = androidx.compose.ui.platform.LocalConfiguration.current.screenWidthDp
    val widthDp: Dp = widthPx.dp
    return when {
        widthDp < 600.dp -> WindowWidthClass.COMPACT
        widthDp < 840.dp -> WindowWidthClass.MEDIUM
        else -> WindowWidthClass.EXPANDED
    }
}
