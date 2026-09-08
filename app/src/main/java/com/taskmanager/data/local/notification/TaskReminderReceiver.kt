package com.taskmanager.data.local.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.taskmanager.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * BroadcastReceiver для обработки срабатывания будильника напоминания о задаче.
 */
@AndroidEntryPoint
class TaskReminderReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationManager: LocalNotificationManager

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra("TASK_ID", -1)
        val taskTitle = intent.getStringExtra("TASK_TITLE") ?: "Задача"
        val dueDate = intent.getLongExtra("TASK_DUE_DATE", 0)

        if (taskId == -1L) return

        // Показываем уведомление о напоминании
        showTaskNotification(context, taskId, taskTitle, dueDate)
    }

    private fun showTaskNotification(
        context: Context,
        taskId: Long,
        title: String,
        dueDate: Long
    ) {
        if (!notificationManager.hasNotificationPermission()) {
            return
        }

        val notificationIntent = Intent(context, com.taskmanager.presentation.MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("TASK_ID", taskId)
        }

        val pendingIntent = android.app.PendingIntent.getActivity(
            context,
            (LocalNotificationManager.REQUEST_CODE_PREFIX + taskId.toInt()),
            notificationIntent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )

        val priority = NotificationCompat.PRIORITY_HIGH

        val notification = NotificationCompat.Builder(context, LocalNotificationManager.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("📋 Напоминание о задаче")
            .setContentText(title)
            .setSubText(if (dueDate > 0) "Дедлайн: ${android.text.format.DateFormat.format("dd MMM yyyy", dueDate)}" else null)
            .setPriority(priority)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 250, 250, 250))
            .build()

        NotificationManagerCompat.from(context).notify(
            (LocalNotificationManager.REQUEST_CODE_PREFIX + taskId.toInt()),
            notification
        )
    }
}
