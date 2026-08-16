package com.taskmanager.domain.usecase.timetracking

import com.taskmanager.domain.model.TimeEntry
import com.taskmanager.domain.model.TimeSpentSummary
import javax.inject.Inject

/**
 * Aggregates time entries into actual-time vs estimate (issue 36).
 * Pure function over a list of entries.
 */
class GetTaskTimeSummaryUseCase @Inject constructor() {

    operator fun invoke(taskId: Long, entries: List<TimeEntry>, estimateMinutes: Int? = null): TimeSpentSummary {
        val taskEntries = entries.filter { it.taskId == taskId }
        val total = taskEntries.sumOf { it.durationMinutes }
        return TimeSpentSummary(
            taskId = taskId,
            totalMinutes = total,
            entryCount = taskEntries.size,
            estimateMinutes = estimateMinutes
        )
    }
}
