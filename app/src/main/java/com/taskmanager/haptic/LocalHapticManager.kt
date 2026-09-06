package com.taskmanager.haptic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Provides [HapticManager] via CompositionLocal for easy access in Composables.
 */
val LocalHapticManager = staticCompositionLocalOf<HapticManager?> { null }

/**
 * Convenience composable to get a haptic lambda.
 * Usage: val haptic = rememberHaptic(); haptic(HapticType.LIGHT)
 */
@Composable
fun rememberHaptic(hapticManager: HapticManager = LocalHapticManager.current ?: error("No HapticManager provided")): (HapticType) -> Unit {
    return remember(hapticManager) {
        { type -> hapticManager.perform(type) }
    }
}
