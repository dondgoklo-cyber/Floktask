package com.taskmanager

import android.app.Application
import android.os.Build
import com.taskmanager.data.repository.FinanceDataSeeder
import com.taskmanager.startup.AppInitializer
import com.taskmanager.startup.StartupTracker
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

@HiltAndroidApp
class TaskManagerApp : Application() {

    @javax.inject.Inject
    lateinit var financeDataSeeder: FinanceDataSeeder

    private val startupTracker = StartupTracker()

    override fun onCreate() {
        super.onCreate()
        financeDataSeeder.seedIfNeeded()
        
        startupTracker.begin()
        val initializer = AppInitializer(this)
        // Critical (blocking): logging tree.
        initializer.initCritical(BuildConfig.DEBUG)
        // Non-critical: deferred off the main thread
        initializer.initNonCritical()
        startupTracker.end("application")

        // Глосальный перехватчики крашей — записывает стеки в файл
        val oldHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val sw = StringWriter()
                throwable.printStackTrace(PrintWriter(sw))
                val crashLog = """
                    === краш WOLFTASK ===
                    время: ${System.currentTimeMillis()}
                    поток: ${thread.name}
                    Android API: ${Build.VERSION.SDK_INT}
                    устройство: ${Build.MANUFACTURER} ${Build.MODEL}

                    Стек:
                    $sw
                """.trimIndent()
                val file = File(getExternalFilesDir(null), "crash_log.txt")
                file.writeText(crashLog)
            } catch (_: Throwable) {}
            oldHandler?.uncaughtException(thread, throwable)
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
