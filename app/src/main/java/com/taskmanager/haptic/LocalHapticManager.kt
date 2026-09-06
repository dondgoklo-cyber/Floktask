package com.taskmanager.haptic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.hilt.navigation.compose.hiltViewModel

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
inline fun <reified T : HapticManager> rememberHaptic(): (HapticType) -> Unit {
    val hapticManager: T = hiltViewModel()
    return remember(hapticManager) {
        { type -> hapticManager.perform(type) }
    }
}
