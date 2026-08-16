package com.taskmanager.domain.usecase.recurrence

import com.taskmanager.domain.model.RecurrenceRule
import com.taskmanager.domain.model.Task
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject

/**
 * Generates the next instance of a recurring task after completion
 * (issue 21: RecurrenceRule existed but no logic created future instances).
 *
 * Returns the next task with an un-completed state and an advanced deadline,
 * or null if the task is not recurring.
 */
class RecurrenceScheduler @Inject constructor() {

    /**
     * Compute the next occurrence instant for [rule] starting from [from].
     * CUSTOM is not supported here (needs its own rule definition).
     */
    fun nextOccurrence(rule: RecurrenceRule, from: Instant, zone: ZoneId = ZoneId.systemDefault()): Instant? =
        when (rule) {
            RecurrenceRule.DAILY -> from.plus(1, ChronoUnit.DAYS)
            RecurrenceRule.WEEKLY -> from.plus(7, ChronoUnit.DAYS)
            RecurrenceRule.MONTHLY -> from.atZone(zone).plusMonths(1).toInstant()
            RecurrenceRule.YEARLY -> from.atZone(zone).plusYears(1).toInstant()
            RecurrenceRule.CUSTOM -> null
        }

    /**
     * Build the next instance of [task] after its completion, advancing the
     * deadline (and reminder) per the recurrence rule. Returns null if the
     * task is not recurring.
     */
    fun nextInstance(task: Task, completedAt: Instant = Instant.now(), zone: ZoneId = ZoneId.systemDefault()): Task? {
        val rule = task.recurrenceRule ?: return null
        val base = task.deadline ?: completedAt
        val nextDeadline = nextOccurrence(rule, base, zone) ?: return null
        val nextReminder = task.reminderDate?.let { nextOccurrence(rule, it, zone) }
        return task.copy(
            id = null,
            isCompleted = false,
            deadline = nextDeadline,
            reminderDate = nextReminder,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }
}
