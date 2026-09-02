package com.taskmanager.domain.usecase.schedule

import com.taskmanager.domain.model.Task
import com.taskmanager.domain.usecase.conflict.TimeInterval
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

/**
 * A task plus its estimated duration, used by the auto-scheduler.
 */
data class SchedulableTask(
    val task: Task,
    val estimatedMinutes: Int
)

/**
 * The result of auto-scheduling: proposed (start, end) slots for tasks,
 * plus the tasks that could not be placed (no room).
 */
data class AutoScheduleResult(
    val placements: List<TaskPlacement>,
    val unscheduled: List<SchedulableTask>
)

data class TaskPlacement(
    val task: Task,
    val start: Instant,
    val end: Instant
)

/**
 * Greedy auto-scheduler (issue 16): packs tasks into the free gaps of a day,
 * respecting existing busy intervals. Tasks are ordered by priority (HIGH→LOW)
 * then by earliest deadline, and placed into the first gap that fits.
 *
 * Working window is [workStartHour, workEndHour] on [date], in the given zone.
 */
class AutoScheduleTasksUseCase @Inject constructor() {

    fun invoke(
        tasks: List<SchedulableTask>,
        date: LocalDate,
        busy: List<TimeInterval>,
        zone: ZoneId = ZoneId.of("UTC"),
        workStartHour: Int = 9,
        workEndHour: Int = 18,
        slotMinutes: Int = 15
    ): AutoScheduleResult {
        require(workEndHour > workStartHour) { "workEndHour must be after workStartHour" }
        require(slotMinutes > 0) { "slotMinutes must be positive" }

        val dayStart = date.atTime(workStartHour, 0).atZone(zone).toInstant()
        val dayEnd = date.atTime(workEndHour, 0).atZone(zone).toInstant()

        // Build free intervals by subtracting busy from the working window.
        val sortedBusy = busy.filter { it.start < dayEnd && it.end > dayStart }
            .sortedBy { it.start }
        val free = computeFreeIntervals(dayStart, dayEnd, sortedBusy)

        // Order tasks: priority then deadline.
        val ordered = tasks.sortedWith(
            compareBy<SchedulableTask> { it.task.priority.value }
                .thenBy(nullsLast()) { it.task.deadline }
        )

        val placements = mutableListOf<TaskPlacement>()
        val unscheduled = mutableListOf<SchedulableTask>()
        // Track remaining free capacity (mutable copies of end times).
        val freeState = free.map { it.start to it.end }.toMutableList()

        for (item in ordered) {
            val durationMillis = item.estimatedMinutes * 60_000L
            val placed = freeState.indexOfFirst { (start, end) ->
                end.toEpochMilli() - start.toEpochMilli() >= durationMillis
            }
            if (placed == -1) {
                unscheduled.add(item)
                continue
            }
            val (start, end) = freeState[placed]
            val placementEnd = Instant.ofEpochMilli(start.toEpochMilli() + durationMillis)
            placements.add(TaskPlacement(item.task, start, placementEnd))
            // Shrink the remaining free interval.
            freeState[placed] = placementEnd to end
        }

        return AutoScheduleResult(placements = placements, unscheduled = unscheduled)
    }

    /**
     * Subtracts [busy] (sorted, clipped to [windowStart, windowEnd]) from the
     * working window, returning the resulting free intervals.
     */
    private fun computeFreeIntervals(
        windowStart: Instant,
        windowEnd: Instant,
        busy: List<TimeInterval>
    ): List<TimeInterval> {
        if (busy.isEmpty()) return listOf(TimeInterval(windowStart, windowEnd))

        val free = mutableListOf<TimeInterval>()
        var cursor = windowStart
        for (interval in busy) {
            val clippedStart = if (interval.start.isAfter(cursor)) interval.start else cursor
            if (clippedStart.isAfter(cursor)) {
                free.add(TimeInterval(cursor, clippedStart))
            }
            if (interval.end.isAfter(cursor)) {
                cursor = interval.end
            }
            if (cursor.isAfter(windowEnd) || cursor == windowEnd) break
        }
        if (cursor.isBefore(windowEnd)) {
            free.add(TimeInterval(cursor, windowEnd))
        }
        return free
    }
}
