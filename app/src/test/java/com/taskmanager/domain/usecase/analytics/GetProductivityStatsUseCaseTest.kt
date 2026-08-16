package com.taskmanager.domain.usecase.analytics

import com.taskmanager.domain.model.Priority
import com.taskmanager.domain.model.Task
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class GetProductivityStatsUseCaseTest {

    private val zone = ZoneId.systemDefault()
    private val useCase = GetProductivityStatsUseCase(
        object : com.taskmanager.domain.repository.TaskRepository {
            override suspend fun createTask(task: Task): Long = 0
            override suspend fun getTaskById(id: Long): Task? = null
            override suspend fun updateTask(task: Task) {}
            override suspend fun deleteTask(id: Long) {}
            override fun getAllTasks() = kotlinx.coroutines.flow.flowOf(emptyList<Task>())
            override fun getTasksByProject(projectId: Long) = kotlinx.coroutines.flow.flowOf(emptyList<Task>())
            override fun getCompletedTasks() = kotlinx.coroutines.flow.flowOf(emptyList<Task>())
            override fun getIncompleteTasks() = kotlinx.coroutines.flow.flowOf(emptyList<Task>())
            override fun searchTasks(query: String) = kotlinx.coroutines.flow.flowOf(emptyList<Task>())
        }
    )

    private fun taskCompletedOn(date: LocalDate): Task {
        val instant = date.atStartOfDay(zone).plusHours(10).toInstant()
        return Task(
            title = "t",
            isCompleted = true,
            updatedAt = instant,
            createdAt = instant
        )
    }

    @Test
    fun `empty task list yields zero stats`() {
        val stats = useCase.compute(emptyList())
        assertEquals(0, stats.totalCompleted)
        assertEquals(0, stats.currentStreak)
        assertEquals(0, stats.longestStreak)
    }

    @Test
    fun `current streak counts consecutive days ending today`() {
        val today = LocalDate.now(zone)
        val tasks = listOf(
            taskCompletedOn(today),
            taskCompletedOn(today.minusDays(1)),
            taskCompletedOn(today.minusDays(2))
        )
        val stats = useCase.compute(tasks)
        assertEquals(3, stats.currentStreak)
    }

    @Test
    fun `streak breaks on a gap`() {
        val today = LocalDate.now(zone)
        val tasks = listOf(
            taskCompletedOn(today),
            taskCompletedOn(today.minusDays(2)) // gap yesterday
        )
        val stats = useCase.compute(tasks)
        assertEquals(1, stats.currentStreak)
        assertEquals(1, stats.longestStreak)
    }

    @Test
    fun `longest streak detected across history`() {
        val today = LocalDate.now(zone)
        val start = today.minusDays(50)
        val tasks = (0 until 5).map { taskCompletedOn(start.plusDays(it.toLong())) } +
            listOf(taskCompletedOn(today))
        val stats = useCase.compute(tasks)
        assertEquals(5, stats.longestStreak)
    }

    @Test
    fun `heatmap has fixed window size`() {
        val today = LocalDate.now(zone)
        val stats = useCase.compute(listOf(taskCompletedOn(today)))
        assertEquals(GetProductivityStatsUseCase.HEATMAP_WINDOW_DAYS, stats.dailyCompletion.size)
        assertEquals(1, stats.dailyCompletion[today])
    }

    @Test
    fun `weekly report has 7 entries ordered oldest first`() {
        val today = LocalDate.now(zone)
        val stats = useCase.compute(listOf(taskCompletedOn(today)))
        assertEquals(7, stats.weeklyReport.size)
        assertEquals(today.minusDays(6), stats.weeklyReport.first().date)
        assertEquals(today, stats.weeklyReport.last().date)
    }

    @Test
    fun `incomplete tasks excluded`() {
        val today = LocalDate.now(zone)
        val incomplete = Task(title = "x", isCompleted = false, updatedAt = today.atStartOfDay(zone).toInstant())
        val stats = useCase.compute(listOf(incomplete))
        assertEquals(0, stats.totalCompleted)
        assertTrue(stats.dailyCompletion.all { it.value == 0 })
    }
}
