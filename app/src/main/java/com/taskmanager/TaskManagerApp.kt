package com.taskmanager

import android.app.Application
import android.os.Build
import com.taskmanager.data.repository.FinanceDataSeeder
import dagger.hilt.android.HiltAndroidApp
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
        financeDataSeeder.seedIfNeeded()
        
        // Глобальный перехватчик крашей — записывает стеки в файл
        val oldHandler = Thread.getDefaultUncaughtExceptionHandler()

        // Глосальный перехватчики крашей — записывает стеки в файл
        val oldHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val sw = StringWriter()
                throwable.printStackTrace(PrintWriter(sw))
                val crashLog = """
                    === Crash Report ===
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
