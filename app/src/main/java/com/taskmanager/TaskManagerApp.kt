package com.taskmanager

import android.app.Application
import android.os.Build
import com.taskmanager.data.repository.FinanceDataSeeder
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

@HiltAndroidApp
class TaskManagerApp : Application() {

    @javax.inject.Inject
    lateinit var financeDataSeeder: FinanceDataSeeder

    override fun onCreate() {
        super.onCreate()

        // Seed in background to avoid ANR
        CoroutineScope(Dispatchers.IO).launch {
            try {
                financeDataSeeder.seedIfNeeded()
            } catch (e: Throwable) {
                Timber.e(e)
            }
        }

        // Global exception handler
        val oldHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val sw = StringWriter()
                throwable.printStackTrace(PrintWriter(sw))
                val crashLog = """
                    === Crash Report ===
                    time: ${System.currentTimeMillis()}
                    thread: ${thread.name}
                    Android API: ${Build.VERSION.SDK_INT}
                    device: ${Build.MANUFACTURER} ${Build.MODEL}

                    Stack:
                    $sw
                """.trimIndent()
                val dir = getExternalFilesDir(null) ?: filesDir
                val file = File(dir, "crash_log.txt")
                file.appendText("\n\n$crashLog")
            } catch (_: Throwable) {}
            oldHandler?.uncaughtException(thread, throwable)
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
