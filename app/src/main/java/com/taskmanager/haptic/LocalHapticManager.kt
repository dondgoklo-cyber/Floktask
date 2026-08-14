package com.taskmanager.haptic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.taskmanager.security.UserPrefs
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Provides [HapticManager] via CompositionLocal for easy access in Composables.
 * Inject through a composable entry point.
 */
val LocalHapticManager = staticCompositionLocalOf<HapticManager?> { null }

/**
 * Convenience composable to get a haptic lambda.
 * Usage: val haptic = rememberHaptic(); haptic(HapticType.LIGHT)
 */
@Composable
fun rememberHaptic(): (HapticType) -> Unit {
    val context = LocalContext.current
    return remember(context) {
        val prefs = UserPrefs(context)
        val manager = HapticManager(context)
        val lambda: (HapticType) -> Unit = { type ->
            if (prefs.hapticEnabled) manager.perform(type)
        }
        lambda
    }
}
