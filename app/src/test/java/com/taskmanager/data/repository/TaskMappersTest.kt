package com.taskmanager.data.repository

import com.taskmanager.data.local.entity.TaskEntity
import com.taskmanager.domain.model.EisenhowerQuadrant
import com.taskmanager.domain.model.Priority
import com.taskmanager.domain.model.RecurrenceRule
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.model.TaskStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.Instant

class TaskMappersTest {

    @Test
    fun task_toEntity_maps_priority_and_nullable_fields() {
        val now = Instant.parse("2026-08-14T10:00:00Z")
        val task = Task(
            id = 5,
            title = "Write spec",
            description = null,
            priority = Priority.HIGH,
            status = TaskStatus.IN_PROGRESS,
            deadline = now,
            startTime = now,
            durationMinutes = 60,
            pomodoroEstimate = 2,
            eisenhowerQuadrant = EisenhowerQuadrant.DO_NOW,
            recurrenceRule = RecurrenceRule.WEEKLY,
            createdAt = now,
            updatedAt = now
        )
        val entity = task.toEntity()
        assertEquals(5L, entity.id)
        assertEquals("Write spec", entity.title)
        assertEquals(Priority.HIGH.value, entity.priority)
        assertEquals("IN_PROGRESS", entity.status)
        assertEquals(now.toEpochMilli(), entity.deadline)
        assertEquals(now.toEpochMilli(), entity.startTime)
        assertEquals(60L, entity.durationMinutes)
        assertEquals(2, entity.pomodoroEstimate)
        assertEquals("DO_NOW", entity.eisenhowerQuadrant)
        assertEquals("WEEKLY", entity.recurrenceRule)
    }

    @Test
    fun entity_toDomain_resolves_priority_and_recurrence() {
        val entity = TaskEntity(
            id = 7,
            title = "Review PR",
            priority = 2,
            status = "DONE",
            recurrenceRule = "MONTHLY",
            deadline = 1_700_000_000_000L,
            startTime = 1_700_000_000_000L,
            durationMinutes = 90,
            pomodoroEstimate = 3,
            eisenhowerQuadrant = "SCHEDULE",
            createdAt = 1_600_000_000_000L,
            updatedAt = 1_700_000_000_000L
        )
        val domain = entity.toDomain()
        assertEquals(7L, domain.id)
        assertEquals(Priority.MEDIUM, domain.priority)
        assertEquals(TaskStatus.DONE, domain.status)
        assertEquals(RecurrenceRule.MONTHLY, domain.recurrenceRule)
        assertEquals(1_700_000_000_000L, domain.deadline?.toEpochMilli())
        assertEquals(90L, domain.durationMinutes)
        assertEquals(3, domain.pomodoroEstimate)
        assertEquals(EisenhowerQuadrant.SCHEDULE, domain.eisenhowerQuadrant)
    }

    @Test
    fun entity_toDomain_handles_unknown_recurrence_gracefully() {
        val entity = TaskEntity(id = 1, title = "x", recurrenceRule = "BOGUS")
        assertNull(entity.toDomain().recurrenceRule)
    }

    @Test
    fun entity_toDomain_defaults_to_none_priority_for_unknown_value() {
        val entity = TaskEntity(id = 1, title = "x", priority = 99)
        assertEquals(Priority.NONE, entity.toDomain().priority)
    }

    @Test
    fun entity_toDomain_defaults_to_todo_status_for_unknown_value() {
        val entity = TaskEntity(id = 1, title = "x", status = "BOGUS")
        assertEquals(TaskStatus.TODO, entity.toDomain().status)
    }

    @Test
    fun entity_toDomain_returns_null_eisenhower_quadrant_when_unset() {
        val entity = TaskEntity(id = 1, title = "x", eisenhowerQuadrant = null)
        assertNull(entity.toDomain().eisenhowerQuadrant)
    }
}
