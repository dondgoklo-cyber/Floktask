package com.taskmanager.presentation.screens.focus

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.taskmanager.R

class PomodoroReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_TIMER_COMPLETE = "com.taskmanager.POMODORO_COMPLETE"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_TIMER_COMPLETE) {
            // Получаем данные из SharedPreferences
            val prefs = context.getSharedPreferences(
                PomodoroService.PREFS_NAME,
                Context.MODE_PRIVATE
            )

            val taskId = prefs.getLong(PomodoroService.KEY_TASK_ID, -1L)
            val duration = prefs.getInt(PomodoroService.KEY_DURATION, 25)
            val type = prefs.getString(PomodoroService.KEY_TYPE, "WORK") ?: "WORK"

            // Сохраняем PomodoroSession через WorkManager
            val saveWork = OneTimeWorkRequestBuilder<SavePomodoroWorker>()
                .setInputData(workDataOf(
                    "taskId" to (if (taskId == -1L) null else taskId),
                    "duration" to duration,
                    "type" to type
                ))
                .build()

            WorkManager.getInstance(context).enqueue(saveWork)

            // Показываем уведомление о завершении
            showCompletionNotification(context, type, duration)

            // Очищаем состояние
            prefs.edit()
                .putBoolean(PomodoroService.KEY_IS_ACTIVE, false)
                .apply()
        }
    }

    private fun showCompletionNotification(context: Context, type: String, duration: Int) {
        val title = when (type) {
            "WORK" -> "🍅 Pomodoro завершен!"
            "SHORT_BREAK", "LONG_BREAK" -> "☕ Перерыв окончен!"
            else -> "Таймер завершен"
        }

        val message = when (type) {
            "WORK" -> "Отличная работа! Время для перерыва."
            "SHORT_BREAK" -> "Готов продолжить?"
            "LONG_BREAK" -> "Отдохнул? Пора работать!"
            else -> "Длительность: $duration мин"
        }

        val notification = NotificationCompat.Builder(context, PomodoroService.CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(PomodoroService.NOTIFICATION_ID + 1, notification)
    }
}
