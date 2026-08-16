package com.taskmanager

import android.app.Application
import com.taskmanager.startup.AppInitializer
import com.taskmanager.startup.StartupTracker
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TaskManagerApp : Application() {

    private val startupTracker = StartupTracker()

    override fun onCreate() {
        super.onCreate()
        startupTracker.begin()
        val initializer = AppInitializer(this)
        // Critical (blocking): logging tree.
        initializer.initCritical(BuildConfig.DEBUG)
        // Non-critical: deferred off the main thread (issue 44).
        initializer.initNonCritical()
        startupTracker.end("application")
    }
}
