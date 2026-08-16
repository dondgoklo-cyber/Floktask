package com.taskmanager.domain.usecase.timetracking

import com.taskmanager.domain.model.TimeEntry
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class GetTaskTimeSummaryUseCaseTest {

    private val useCase = GetTaskTimeSummaryUseCase()
    private val now = Instant.parse("2026-01-15T10:00:00Z")

    private fun entry(task: Long, minutes: Int) = TimeEntry(
        taskId = task, start = now, end = now.plusSeconds(60L * minutes),
        durationMinutes = minutes
    )

    @Test
    fun `aggregates duration for a task`() {
        val entries = listOf(entry(1, 25), entry(1, 35), entry(2, 10))
        val summary = useCase(1, entries)
        assertEquals(60, summary.totalMinutes)
        assertEquals(2, summary.entryCount)
    }

    @Test
    fun `variance is actual minus estimate`() {
        val summary = useCase(1, listOf(entry(1, 40)), estimateMinutes = 30)
        assertEquals(10, summary.varianceMinutes)
    }

    @Test
    fun `no estimate yields null variance`() {
        val summary = useCase(1, listOf(entry(1, 10)))
        assertEquals(null, summary.varianceMinutes)
    }

    @Test
    fun `no entries yields zero`() {
        val summary = useCase(1, emptyList())
        assertEquals(0, summary.totalMinutes)
        assertEquals(0, summary.entryCount)
    }

    @Test
    fun `filters to the target task only`() {
        val entries = listOf(entry(1, 20), entry(2, 999), entry(1, 5))
        assertEquals(25, useCase(1, entries).totalMinutes)
    }
}
