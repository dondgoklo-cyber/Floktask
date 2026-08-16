package com.taskmanager.presentation.screens.focus

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.taskmanager.R
import com.taskmanager.presentation.MainActivity
import java.util.Timer
import java.util.TimerTask

class PomodoroService : Service() {

    companion object {
        const val CHANNEL_ID = "pomodoro_timer"
        const val NOTIFICATION_ID = 1001
        const val EXTRA_TASK_ID = "taskId"
        const val EXTRA_DURATION = "duration"
        const val EXTRA_TYPE = "type"

        const val PREFS_NAME = "pomodoro_prefs"
        const val KEY_END_TIME = "endTime"
        const val KEY_TASK_ID = "taskId"
        const val KEY_DURATION = "duration"
        const val KEY_TYPE = "type"
        const val KEY_IS_ACTIVE = "isActive"
    }

    private var timerTask: TimerTask? = null
    private var timer: Timer? = null
    private var wakeLock: PowerManager.WakeLock? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val taskId = intent?.getLongExtra(EXTRA_TASK_ID, -1L) ?: -1L
        val duration = intent?.getIntExtra(EXTRA_DURATION, 25) ?: 25
        val type = intent?.getStringExtra(EXTRA_TYPE) ?: "WORK"

        // Показываем notification
        val notification = createNotification(taskId, duration * 60, type)
        startForeground(NOTIFICATION_ID, notification)

        // Запускаем таймер
        startTimer(duration, taskId, type)

        return START_STICKY // ✅ Перезапуск при убийстве процесса
    }

    private fun startTimer(durationMinutes: Int, taskId: Long, type: String) {
        val endTime = System.currentTimeMillis() + durationMinutes * 60 * 1000

        // Сохраняем состояние
        saveState(endTime, taskId, durationMinutes, type)

        // Планируем broadcast при завершении
        scheduleCompletionBroadcast(endTime)

        // Wake lock чтобы таймер работал в фоне
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "Floktask::PomodoroWakeLock"
        )
        wakeLock?.acquire((durationMinutes + 1) * 60 * 1000L)

        // Обновляем notification каждую секунду
        timer = Timer()
        timerTask = object : TimerTask() {
            override fun run() {
                val remaining = ((endTime - System.currentTimeMillis()) / 1000).toInt()
                if (remaining > 0) {
                    updateNotification(remaining, type)
                } else {
                    // Таймер завершен
                    timer?.cancel()
                    stopSelf()
                }
            }
        }
        timer?.scheduleAtFixedRate(timerTask, 0, 1000)
    }

    private fun scheduleCompletionBroadcast(endTime: Long) {
        val alarmManager = getSystemService(ALARM_SERVICE) as android.app.AlarmManager
        val intent = Intent(this, PomodoroReceiver::class.java).apply {
            action = PomodoroReceiver.ACTION_TIMER_COMPLETE
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Точный alarm даже в Doze mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                android.app.AlarmManager.RTC_WAKEUP,
                endTime,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                android.app.AlarmManager.RTC_WAKEUP,
                endTime,
                pendingIntent
            )
        }
    }

    private fun saveState(endTime: Long, taskId: Long, duration: Int, type: String) {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .edit()
            .putLong(KEY_END_TIME, endTime)
            .putLong(KEY_TASK_ID, taskId)
            .putInt(KEY_DURATION, duration)
            .putString(KEY_TYPE, type)
            .putBoolean(KEY_IS_ACTIVE, true)
            .apply()
    }

    private fun clearState() {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .edit()
            .remove(KEY_END_TIME)
            .remove(KEY_TASK_ID)
            .remove(KEY_DURATION)
            .remove(KEY_TYPE)
            .putBoolean(KEY_IS_ACTIVE, false)
            .apply()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Pomodoro Timer",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Показывает активный Pomodoro таймер"
                setShowBadge(false)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(taskId: Long, remainingSeconds: Int, type: String): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val typeText = when (type) {
            "WORK" -> "Работа"
            "SHORT_BREAK" -> "Короткий перерыв"
            "LONG_BREAK" -> "Длинный перерыв"
            else -> "Pomodoro"
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("$typeText")
            .setContentText(formatTime(remainingSeconds))
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Замени на свою иконку
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setProgress(100, 0, true) // Indeterminate progress
            .build()
    }

    private fun updateNotification(remainingSeconds: Int, type: String) {
        val notification = createNotification(-1L, remainingSeconds, type)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun formatTime(seconds: Int): String {
        val mins = seconds / 60
        val secs = seconds % 60
        return String.format("%02d:%02d", mins, secs)
    }

    override fun onDestroy() {
        timerTask?.cancel()
        timer?.cancel()
        wakeLock?.let {
            if (it.isHeld) it.release()
        }
        clearState()
        super.onDestroy()
    }
}
