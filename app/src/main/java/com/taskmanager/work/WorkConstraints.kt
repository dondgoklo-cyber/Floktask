package com.taskmanager.work

import androidx.work.Constraints
import androidx.work.NetworkType

/**
 * Factory for battery-friendly [Constraints] applied to background work
 * (issue 43: sync/notifications can drain battery). Centralizes the policy
 * so workers stay consistent.
 */
object WorkConstraints {

    /**
     * For sync work: requires unmetered network + battery not low.
     * (Cloud sync shouldn't burn mobile data.)
     */
    fun forSync(): Constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.UNMETERED)
        .setRequiresBatteryNotLow(true)
        .build()

    /**
     * For backup work: requires charging + device idle (best done overnight).
     */
    fun forBackup(): Constraints = Constraints.Builder()
        .setRequiresCharging(true)
        .setRequiresDeviceIdle(true)
        .setRequiresBatteryNotLow(true)
        .build()

    /**
     * For lightweight work (e.g. local notifications): just avoid low battery.
     */
    fun forLightweight(): Constraints = Constraints.Builder()
        .setRequiresBatteryNotLow(true)
        .build()
}
