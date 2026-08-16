package com.taskmanager.domain.usecase.schedule

import com.taskmanager.domain.model.Priority
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.usecase.conflict.TimeInterval
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class AutoScheduleTasksUseCaseTest {

    private val zone = ZoneId.of("UTC")
    private val date = LocalDate.of(2026, 1, 15)
    private val useCase = AutoScheduleTasksUseCase()

    private fun task(priority: Priority = Priority.LOW): Task =
        Task(title = "t", priority = priority, createdAt = Instant.now(), updatedAt = Instant.now())

    private fun schedulable(task: Task, minutes: Int) = SchedulableTask(task, minutes)

    @Test
    fun `places tasks in free gaps`() {
        val tasks = listOf(schedulable(task(), 60), schedulable(task(), 30))
        val result = useCase(tasks, date, emptyList(), zone)
        assertEquals(2, result.placements.size)
        assertTrue(result.unscheduled.isEmpty())
    }

    @Test
    fun `reports unscheduled when no gap fits`() {
        val busy = listOf(
            TimeInterval(
                date.atTime(10, 0).atZone(zone).toInstant(),
                date.atTime(11, 0).atZone(zone).toInstant()
            )
        )
        val tasks = listOf(schedulable(task(), 120))
        val result = useCase(tasks, date, busy, zone, workStartHour = 9, workEndHour = 12)
        assertEquals(0, result.placements.size)
        assertEquals(1, result.unscheduled.size)
    }

    @Test
    fun `high priority scheduled before low`() {
        val high = schedulable(task(Priority.HIGH), 60)
        val low = schedulable(task(Priority.LOW), 60)
        val result = useCase(listOf(low, high), date, emptyList(), zone)
        // HIGH placed first → 9:00-10:00; LOW → 10:00-11:00
        assertEquals(high.task.title, result.placements[0].task.title)
        assertEquals(low.task.title, result.placements[1].task.title)
    }

    @Test
    fun `placements do not overlap`() {
        val tasks = listOf(
            schedulable(task(), 60),
            schedulable(task(), 60),
            schedulable(task(), 60)
        )
        val result = useCase(tasks, date, emptyList(), zone)
        for (i in 1 until result.placements.size) {
            val prev = result.placements[i - 1]
            val curr = result.placements[i]
            assertTrue(!curr.start.isBefore(prev.end))
        }
    }

    @Test
    fun `empty tasks yields empty result`() {
        val result = useCase(emptyList(), date, emptyList(), zone)
        assertTrue(result.placements.isEmpty())
        assertTrue(result.unscheduled.isEmpty())
    }
}
