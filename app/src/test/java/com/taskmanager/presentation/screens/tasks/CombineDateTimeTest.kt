package com.taskmanager.presentation.screens.tasks

import com.taskmanager.domain.model.EisenhowerQuadrant
import com.taskmanager.domain.model.Priority
import com.taskmanager.domain.model.RecurrenceRule
import com.taskmanager.domain.model.TaskStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

class CombineDateTimeTest {

    private val zone = ZoneId.of("Europe/Moscow")

    private fun state(
        deadlineDate: LocalDate? = null,
        startTime: LocalTime? = null
    ): TaskFormState = TaskFormState(
        title = "",
        deadlineDate = deadlineDate,
        startTime = startTime
    )

    @Test
    fun `date without time - deadline is end of day, startTime null`() {
        val date = LocalDate.of(2026, 3, 15)
        val (deadline, startTime) = combineTaskDateTime(state(date), zone)
        assertNull(startTime)
        assertEquals(
            date.atTime(23, 59, 59, 999_999_999).atZone(zone).toInstant(),
            deadline
        )
    }

    @Test
    fun `date plus time - startTime and deadline are different`() {
        val date = LocalDate.of(2026, 3, 15)
        val time = LocalTime.of(10, 0)
        val (deadline, startTime) = combineTaskDateTime(state(date, time), zone)
        assertEquals(
            date.atTime(time).atZone(zone).toInstant(),
            startTime
        )
        assertEquals(
            date.atTime(23, 59, 59, 999_999_999).atZone(zone).toInstant(),
            deadline
        )
        // Critical: startTime MUST be before deadline (regression for deadline==startTime bug).
        assertTrue("startTime must be before deadline", startTime!!.isBefore(deadline!!))
    }

    @Test
    fun `no date - both null`() {
        val (deadline, startTime) = combineTaskDateTime(state(null, LocalTime.of(10, 0)), zone)
        assertNull(deadline)
        assertNull(startTime)
    }

    @Test
    fun `start without deadline is not possible in this model - only date drives both`() {
        // In the form model startTime requires deadlineDate; without date, startTime is ignored.
        val (deadline, startTime) = combineTaskDateTime(state(null, LocalTime.of(10, 0)), zone)
        assertNull(deadline)
        assertNull(startTime)
    }

    @Test
    fun `start equals end of day when start time is 23_59_59`() {
        val date = LocalDate.of(2026, 3, 15)
        val time = LocalTime.of(23, 59, 59)
        val (deadline, startTime) = combineTaskDateTime(state(date, time), zone)
        assertTrue(startTime!! == date.atTime(time).atZone(zone).toInstant())
        assertTrue(startTime.isBefore(deadline!!) || startTime == deadline.minusNanos(1))
    }

    @Test
    fun `timezone affects the produced instants`() {
        val date = LocalDate.of(2026, 3, 15)
        val time = LocalTime.of(10, 0)
        val (deadlineUtc, startUtc) = combineTaskDateTime(state(date, time), ZoneId.of("UTC"))
        val (deadlineMsk, startMsk) = combineTaskDateTime(state(date, time), zone)
        // Same local date+time, different zones → different instants (MSK is UTC+3).
        assertEquals(3 * 3600L, startUtc!!.epochSecond - startMsk!!.epochSecond)
    }

    @Test
    fun `DST transition - spring forward 2026-03-29 in Moscow does not apply (Moscow no DST) but UTC+3 stable`() {
        // Moscow fixed UTC+3, no DST. Verify date across day boundary is consistent.
        val date = LocalDate.of(2026, 3, 29)
        val (deadline, startTime) = combineTaskDateTime(state(date, LocalTime.of(2, 30)), zone)
        assertEquals(
            date.atTime(23, 59, 59, 999_999_999).atZone(zone).toInstant(),
            deadline
        )
    }
}
