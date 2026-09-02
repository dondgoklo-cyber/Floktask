package com.taskmanager.domain.notification

/**
 * Domain-level contract for scheduling local reminders.
 * Keeps the domain layer free of Android (AlarmManager) dependencies; the data/notification
 * layer provides the implementation (AlarmScheduler). Use cases that change task lifecycle
 * (complete, snooze, delete) coordinate DB + reminder through this abstraction.
 */
interface ReminderScheduler {
    /** Schedule a reminder for [taskId] with [title] to fire at [triggerAtMillis] epoch millis. */
    fun scheduleReminder(taskId: Long, title: String, triggerAtMillis: Long)

    /** Cancel any scheduled reminder for [taskId]. */
    fun cancelReminder(taskId: Long)

    /** Reschedule all active future reminders (e.g. after device reboot or app update). */
    fun rescheduleAllReminders()
}
