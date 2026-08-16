package com.taskmanager.presentation.screens.focusmode

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Toggles Do-Not-Disturb for focus mode (issue 20).
 *
 * On Android 7+ changing the interruption filter requires
 * `ACCESS_NOTIFICATION_POLICY` (granted via the system settings intent).
 * Falls back gracefully (no-op) when permission is missing.
 */
@Singleton
class DndHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val notificationManager: NotificationManager
        get() = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val isPolicyAccessGranted: Boolean
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationManager.isNotificationPolicyAccessGranted
        } else {
            false
        }

    /**
     * Enable DND (silence interruptions). Returns true if applied.
     */
    fun enableDnd(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return false
        if (!notificationManager.isNotificationPolicyAccessGranted) return false
        return runCatching {
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE)
            true
        }.getOrDefault(false)
    }

    /**
     * Restore the previous interruption filter (disable DND).
     */
    fun disableDnd(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return false
        if (!notificationManager.isNotificationPolicyAccessGranted) return false
        return runCatching {
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
            true
        }.getOrDefault(false)
    }
}
