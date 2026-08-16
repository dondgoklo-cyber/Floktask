package com.taskmanager.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.taskmanager.data.local.dao.TaskDao
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Планировщик локальных напоминаний через AlarmManager.
 * Полностью оффлайн: точные будильники + перерегистрация после перезагрузки устройства.
 */
@Singleton
class AlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val taskDao: TaskDao
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun scheduleReminder(taskId: Long, title: String, triggerAtMillis: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = buildPendingIntent(taskId, title, ACTION_SHOW)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }
    }

    fun cancelReminder(taskId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(buildPendingIntent(taskId, "", ACTION_SHOW))
    }

    /** Отложить напоминание на [delayMinutes] минут. */
    fun snoozeReminder(taskId: Long, title: String, delayMinutes: Int) {
        val triggerAt = System.currentTimeMillis() + delayMinutes * 60_000L
        scheduleReminder(taskId, title, triggerAt)
    }

    /** Перезапланировать все активные напоминания (после перезагрузки устройства). */
    fun rescheduleAllReminders() {
        scope.launch {
            val now = System.currentTimeMillis()
            taskDao.getAllTasksWithReminders(now).forEach { task ->
                task.reminderDate?.let { triggerAt ->
                    if (triggerAt > now) {
                        scheduleReminder(task.id, task.title, triggerAt)
                    }
                }
            }
        }
    }

    private fun buildPendingIntent(taskId: Long, title: String, action: String): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            this.action = action
            putExtra(EXTRA_TASK_ID, taskId)
            putExtra(EXTRA_TASK_TITLE, title)
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        return when (action) {
            ACTION_COMPLETE -> PendingIntent.getBroadcast(context, taskId.completeRequestCode(), intent, flags)
            else -> PendingIntent.getBroadcast(context, taskId.showRequestCode(), intent, flags)
        }
    }

    private fun Long.showRequestCode(): Int = (this % Int.MAX_VALUE).toInt()
    private fun Long.completeRequestCode(): Int = ((this + 100_000L) % Int.MAX_VALUE).toInt()

    companion object {
        const val EXTRA_TASK_ID = "extra_task_id"
        const val EXTRA_TASK_TITLE = "extra_task_title"
        const val ACTION_SHOW = "com.taskmanager.action.SHOW_REMINDER"
        const val ACTION_COMPLETE = "com.taskmanager.action.COMPLETE_TASK"
        const val ACTION_SNOOZE_5 = "com.taskmanager.action.SNOOZE_5"
        const val ACTION_SNOOZE_15 = "com.taskmanager.action.SNOOZE_15"
        const val ACTION_SNOOZE_30 = "com.taskmanager.action.SNOOZE_30"
        const val ACTION_SNOOZE_60 = "com.taskmanager.action.SNOOZE_60"
    }
}
