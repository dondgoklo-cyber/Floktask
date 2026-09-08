package com.taskmanager.data.local.notification

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.taskmanager.R
import com.taskmanager.domain.model.Task
import com.taskmanager.presentation.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val CHANNEL_ID = "task_deadlines"
        private const val CHANNEL_NAME = "Напоминания о задачах"
        private const val CHANNEL_DESCRIPTION = "Уведомления о приближающихся дедлайнах"
        const val REQUEST_CODE_PREFIX = 1000
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                enableLights(true)
                lightColor = android.graphics.Color.BLUE
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun scheduleTaskReminder(task: Task) {
        if (!hasNotificationPermission()) {
            return
        }

        task.deadline?.let { deadline ->
            val currentTime = System.currentTimeMillis()

            // Планируем уведомление за 1 час до дедлайна
            val reminderTime = deadline - (60 * 60 * 1000) // 1 час до дедлайна
            
            // Если время напоминания уже прошло, но дедлайн ещё не наступил - показываем сейчас
            val triggerTime = when {
                reminderTime > currentTime -> reminderTime
                deadline > currentTime -> currentTime
                else -> return // Дедлайн уже прошёл
            }

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, TaskReminderReceiver::class.java).apply {
                putExtra("TASK_ID", task.id)
                putExtra("TASK_TITLE", task.title)
                putExtra("TASK_DUE_DATE", deadline)
            }
            
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                (REQUEST_CODE_PREFIX + task.id.toInt()),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Планируем точное уведомление
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )

            // Немедленно показываем уведомление, если время уже настало
            if (triggerTime <= currentTime) {
                showTaskNotification(task)
            }
        }
    }

    fun showTaskNotification(task: Task) {
        if (!hasNotificationPermission()) {
            return
        }

        val notificationIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("TASK_ID", task.id)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            (REQUEST_CODE_PREFIX + task.id.toInt()),
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val priority = when (task.priority) {
            com.taskmanager.domain.model.Priority.HIGH -> NotificationCompat.PRIORITY_HIGH
            com.taskmanager.domain.model.Priority.MEDIUM -> NotificationCompat.PRIORITY_DEFAULT
            else -> NotificationCompat.PRIORITY_LOW
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("📋 Напоминание о задаче")
            .setContentText(task.title)
            .setSubText(task.deadline?.let { "Дедлайн: ${android.text.format.DateFormat.format("dd MMM yyyy", it)}" })
            .setPriority(priority)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 250, 250, 250))
            .build()

        NotificationManagerCompat.from(context).notify(
            (REQUEST_CODE_PREFIX + task.id.toInt()),
            notification
        )
    }

    fun cancelTaskReminder(taskId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, TaskReminderReceiver::class.java).apply {
            putExtra("TASK_ID", taskId)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            (REQUEST_CODE_PREFIX + taskId.toInt()),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        pendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
        }

        // Также отменяем уведомление
        NotificationManagerCompat.from(context).cancel(
            (REQUEST_CODE_PREFIX + taskId.toInt())
        )
    }

    fun cancelAllReminders() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        // Отменяем все наши будильники (в диапазоне разумных ID)
        for (i in 0 until 1000) {
            val intent = Intent(context, TaskReminderReceiver::class.java).apply {
                putExtra("TASK_ID", i.toLong())
            }
            
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_PREFIX + i,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )

            pendingIntent?.let {
                alarmManager.cancel(it)
                it.cancel()
            }
        }

        // Отменяем все уведомления из нашего канала
        NotificationManagerCompat.from(context).cancelAll()
    }
}
