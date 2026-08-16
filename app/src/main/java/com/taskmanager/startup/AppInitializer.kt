package com.taskmanager.startup

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Defer non-critical initialization off the main thread to cut cold-start
 * time (issue 44: cold start could be >2s; lazy-init non-critical services).
 *
 * Critical path: Hilt graph + Room (already lazy via Hilt). Deferred:
 * analytics, crash-reporting hooks, etc.
 */
class AppInitializer(private val context: Context) {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Initialize critical, on-startup-blocking components.
     */
    fun initCritical(debug: Boolean) {
        if (debug) {
            Timber.plant(Timber.DebugTree())
        }
    }

    /**
     * Initialize non-critical components off the main thread after launch.
     */
    fun initNonCritical() {
        appScope.launch {
            runCatching { initAnalytics() }
            runCatching { initCrashReporting() }
        }
    }

    private fun initAnalytics() {
        // Firebase Analytics / third-party SDKs would go here.
        Timber.d("Non-critical init: analytics")
    }

    private fun initCrashReporting() {
        // Crashlytics init would go here.
        Timber.d("Non-critical init: crash reporting")
    }
}
