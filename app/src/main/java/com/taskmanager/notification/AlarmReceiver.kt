package com.taskmanager.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.taskmanager.R
import com.taskmanager.data.local.dao.TaskDao
import com.taskmanager.presentation.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

/**
 * Приёмник будильников напоминаний. Показывает уведомление с действиями:
 * выполнить задачу, отложить на 5/15/30/60 минут.
 *
 * Важно: действия Complete и Snooze изменяют состояние БД, а не только уведомление:
 * - Complete: помечает задачу COMPLETED в Room и отменяет запланированный будильник.
 * - Snooze: обновляет reminderDate в Room и перепланирует будильник, чтобы БД и
 *   AlarmManager всегда совпадали (ранее AlarmManager знал одно время, а Room — другое).
 *
 * BroadcastReceiver может быть убит системой, поэтому длительные операции выполняются
 * через goAsync() + application-scoped coroutine scope.
 */
@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    @Inject
    lateinit var taskDao: TaskDao

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val taskId = intent.getLongExtra(AlarmScheduler.EXTRA_TASK_ID, -1L)
        val title = intent.getStringExtra(AlarmScheduler.EXTRA_TASK_TITLE) ?: ""
        if (taskId <= 0L) return

        val pending = goAsync()
        when {
            action == AlarmScheduler.ACTION_SHOW -> {
                showNotification(context, taskId, title)
                pending.finish()
            }
            action == AlarmScheduler.ACTION_COMPLETE -> {
                appScope.launch {
                    try {
                        // Реально завершаем задачу в БД (ранее только закрывалось уведомление).
                        taskDao.setCompleted(taskId, true, Instant.now().toEpochMilli())
                        alarmScheduler.cancelReminder(taskId)
                        NotificationManagerCompat.from(context).cancel(taskId.toInt())
                    } catch (e: Exception) {
                        Log.e("AlarmReceiver", "Complete failed for task $taskId", e)
                    } finally {
                        pending.finish()
                    }
                }
            }
            action.startsWith("com.taskmanager.action.SNOOZE_") -> {
                val minutes = action.substringAfterLast("_").toIntOrNull() ?: 15
                appScope.launch {
                    try {
                        // Единый flow Snooze: обновляем reminderDate в БД, затем перепланируем
                        // будильник, чтобы AlarmManager и Room хранили одно время.
                        val triggerAt = System.currentTimeMillis() + minutes * 60_000L
                        taskDao.updateReminderDate(taskId, triggerAt, Instant.now().toEpochMilli())
                        val resolvedTitle = runCatching { taskDao.getById(taskId)?.title }.getOrNull() ?: title
                        alarmScheduler.scheduleReminder(taskId, resolvedTitle, triggerAt)
                        NotificationManagerCompat.from(context).cancel(taskId.toInt())
                    } catch (e: Exception) {
                        Log.e("AlarmReceiver", "Snooze failed for task $taskId", e)
                    } finally {
                        pending.finish()
                    }
                }
            }
            else -> pending.finish()
        }
    }

    private fun showNotification(context: Context, taskId: Long, title: String) {
        ensureChannel(context)

        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(EXTRA_TASK_ID, taskId)
        }
        val contentPi = PendingIntent.getActivity(
            context, taskId.toInt(), contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        fun snoozeIntent(minutes: Int, action: String): PendingIntent {
            val i = Intent(context, AlarmReceiver::class.java).apply {
                this.action = action
                putExtra(AlarmScheduler.EXTRA_TASK_ID, taskId)
                putExtra(AlarmScheduler.EXTRA_TASK_TITLE, title)
            }
            return PendingIntent.getBroadcast(
                context, (taskId * 100 + minutes).toInt(), i,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        val completeIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmScheduler.ACTION_COMPLETE
            putExtra(AlarmScheduler.EXTRA_TASK_ID, taskId)
        }
        val completePi = PendingIntent.getBroadcast(
            context, "$taskId-complete".hashCode(), completeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_task)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(title)
            .setStyle(NotificationCompat.BigTextStyle().bigText(title))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(contentPi)
            .addAction(R.drawable.ic_stat_task, context.getString(R.string.complete), completePi)
            .addAction(R.drawable.ic_stat_task, context.getString(R.string.snooze_5), snoozeIntent(5, AlarmScheduler.ACTION_SNOOZE_5))
            .addAction(R.drawable.ic_stat_task, context.getString(R.string.snooze_15), snoozeIntent(15, AlarmScheduler.ACTION_SNOOZE_15))
            .addAction(R.drawable.ic_stat_task, context.getString(R.string.snooze_30), snoozeIntent(30, AlarmScheduler.ACTION_SNOOZE_30))
            .addAction(R.drawable.ic_stat_task, context.getString(R.string.snooze_60), snoozeIntent(60, AlarmScheduler.ACTION_SNOOZE_60))

        NotificationManagerCompat.from(context).notify(taskId.toInt(), builder.build())
    }

    private fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.channel_reminders),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.channel_reminders_desc)
            }
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "task_reminders"
        const val EXTRA_TASK_ID = "extra_task_id_alarm"

        /**
         * Application-scoped scope для BroadcastReceiver-операций. goAsync() даёт ~10с,
         * поэтому операции должны быть короткими (DB write). SupervisorJob изолирует ошибки.
         */
        private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }
}
