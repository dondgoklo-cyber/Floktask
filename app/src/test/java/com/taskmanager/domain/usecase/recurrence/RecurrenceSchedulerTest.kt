package com.taskmanager.domain.usecase.recurrence

import com.taskmanager.domain.model.RecurrenceRule
import com.taskmanager.domain.model.Task
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class RecurrenceSchedulerTest {

    private val zone = ZoneId.of("UTC")
    private val scheduler = RecurrenceScheduler()
    private val base = Instant.parse("2026-01-15T10:00:00Z")

    @Test
    fun `non-recurring task yields null`() {
        val task = Task(title = "t", recurrenceRule = null, deadline = base)
        assertNull(scheduler.nextInstance(task))
    }

    @Test
    fun `daily advances by one day`() {
        val task = Task(title = "t", recurrenceRule = RecurrenceRule.DAILY, deadline = base)
        val next = scheduler.nextInstance(task, completedAt = base, zone = zone)
        assertNotNull(next)
        assertEquals(base.plus(1, ChronoUnit.DAYS), next!!.deadline)
    }

    @Test
    fun `weekly advances by seven days`() {
        val task = Task(title = "t", recurrenceRule = RecurrenceRule.WEEKLY, deadline = base)
        val next = scheduler.nextInstance(task, completedAt = base, zone = zone)!!
        assertEquals(base.plus(7, ChronoUnit.DAYS), next.deadline)
    }

    @Test
    fun `monthly advances by one month`() {
        val task = Task(title = "t", recurrenceRule = RecurrenceRule.MONTHLY, deadline = base)
        val next = scheduler.nextInstance(task, completedAt = base, zone = zone)!!
        assertEquals(base.atZone(zone).plusMonths(1).toInstant(), next.deadline)
    }

    @Test
    fun `yearly advances by one year`() {
        val task = Task(title = "t", recurrenceRule = RecurrenceRule.YEARLY, deadline = base)
        val next = scheduler.nextInstance(task, completedAt = base, zone = zone)!!
        assertEquals(base.atZone(zone).plusYears(1).toInstant(), next.deadline)
    }

    @Test
    fun `custom rule yields null`() {
        val task = Task(title = "t", recurrenceRule = RecurrenceRule.CUSTOM, deadline = base)
        assertNull(scheduler.nextInstance(task))
    }

    @Test
    fun `next instance is uncompleted with null id`() {
        val task = Task(id = 5, title = "t", recurrenceRule = RecurrenceRule.DAILY, deadline = base, isCompleted = true)
        val next = scheduler.nextInstance(task, completedAt = base, zone = zone)!!
        assertNull(next.id)
        assertEquals(false, next.isCompleted)
    }

    @Test
    fun `reminder advances with deadline`() {
        val reminder = base.minus(1, ChronoUnit.HOURS)
        val task = Task(title = "t", recurrenceRule = RecurrenceRule.DAILY, deadline = base, reminderDate = reminder)
        val next = scheduler.nextInstance(task, completedAt = base, zone = zone)!!
        assertNotNull(next.reminderDate)
        assertEquals(reminder.plus(1, ChronoUnit.DAYS), next.reminderDate)
    }
}
