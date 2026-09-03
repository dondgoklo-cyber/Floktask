package com.taskmanager.startup

import android.os.SystemClock
import timber.log.Timber

/**
 * Measures cold-start duration for startup optimization (issue 44).
 * Call [begin] in Application.onCreate and [end] in the first frame / activity.
 */
class StartupTracker {

    @Volatile
    private var startElapsed: Long = 0L

    fun begin() {
        startElapsed = SystemClock.elapsedRealtime()
    }

    fun end(label: String = "cold-start") {
        val s = startElapsed
        if (s == 0L) return
        val durationMs = SystemClock.elapsedRealtime() - s
        Timber.i("Startup[$label] took ${durationMs}ms")
    }

    fun durationMs(): Long? =
        if (startElapsed == 0L) null else SystemClock.elapsedRealtime() - startElapsed
}
