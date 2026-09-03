package com.taskmanager.presentation.screens.focusmode

import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages Do-Not-Disturb mode for focus sessions.
 *
 * On Android 6+ (API 23+) changing the interruption filter requires
 * `ACCESS_NOTIFICATION_POLICY` permission (granted via system settings intent).
 * Falls back gracefully (no-op) when permission is missing.
 *
 * IMPORTANT: This implementation captures and restores the previous interruption filter
 * to avoid leaving the device in an unexpected DND state after focus mode exits.
 * Uses SharedPreferences to persist state across process death.
 */
@Singleton
class DndHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val notificationManager: NotificationManager
        get() = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences("dnd_prefs", Context.MODE_PRIVATE)
    }

    /**
     * Stores the previous interruption filter before enabling DND.
     * null means DND was not enabled or state was not captured.
     */
    private var previousFilter: Int?
        get() = prefs.getInt(KEY_PREVIOUS_FILTER, -1).takeIf { it != -1 }
        set(value) {
            prefs.edit().putInt(KEY_PREVIOUS_FILTER, value ?: -1).apply()
        }

    /**
     * Flag to prevent nested DND state changes.
     */
    private var isDndActive: Boolean
        get() = prefs.getBoolean(KEY_DND_ACTIVE, false)
        set(value) {
            prefs.edit().putBoolean(KEY_DND_ACTIVE, value).apply()
        }

    val isPolicyAccessGranted: Boolean
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationManager.isNotificationPolicyAccessGranted
        } else {
            false
        }

    /**
     * Enable DND (silence interruptions).
     * Captures the current interruption filter before changing it.
     * Returns true if DND was successfully enabled.
     *
     * Note: If already enabled, does nothing and returns true.
     */
    fun enableDnd(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return false
        if (!notificationManager.isNotificationPolicyAccessGranted) return false
        
        // Prevent nested calls
        if (isDndActive) return true
        
        return runCatching {
            // Capture current state before changing
            previousFilter = notificationManager.currentInterruptionFilter
            
            // Enable DND
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE)
            isDndActive = true
            true
        }.getOrDefault(false)
    }

    /**
     * Restore the previous interruption filter (disable DND).
     * Returns true if the previous state was successfully restored.
     *
     * Note: If DND was never enabled, does nothing and returns false.
     */
    fun disableDnd(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return false
        if (!notificationManager.isNotificationPolicyAccessGranted) return false
        
        // Prevent nested calls
        if (!isDndActive) return false
        
        return runCatching {
            // Restore previous filter
            previousFilter?.let { filter ->
                notificationManager.setInterruptionFilter(filter)
            } ?: run {
                // Fallback: restore to ALL if we don't know the previous state
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
            }
            
            // Reset state
            previousFilter = null
            isDndActive = false
            true
        }.getOrDefault(false)
    }

    /**
     * Check if DND is currently active through this helper.
     */
    fun isDndEnabled(): Boolean = isDndActive

    /**
     * Reset internal state without changing DND.
     * Useful for cleanup when focus session is abandoned.
     */
    fun reset() {
        previousFilter = null
        isDndActive = false
    }

    companion object {
        private const val KEY_PREVIOUS_FILTER = "previous_filter"
        private const val KEY_DND_ACTIVE = "dnd_active"
    }
}