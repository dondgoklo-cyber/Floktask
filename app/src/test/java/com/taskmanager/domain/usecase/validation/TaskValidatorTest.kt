package com.taskmanager.domain.usecase.validation

import com.taskmanager.domain.model.Task
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test
import java.time.Instant
import java.time.temporal.ChronoUnit

class TaskValidatorTest {

    private val validator = TaskValidator()
    private val now = Instant.parse("2026-01-15T10:00:00Z")

    private fun task(title: String, deadline: Instant? = null, description: String? = null) =
        Task(title = title, deadline = deadline, description = description, createdAt = now, updatedAt = now)

    @Test
    fun `valid task passes`() {
        assertTrue(validator.validate(task("Buy milk"), now = now) is ValidationResult.Valid)
    }

    @Test
    fun `empty title is invalid`() {
        assertTrue(validator.validate(task("   "), now = now) is ValidationResult.Invalid)
    }

    @Test
    fun `title over max length is invalid`() {
        val long = "x".repeat(TaskValidator.MAX_TITLE_LENGTH + 1)
        assertTrue(validator.validate(task(long), now = now) is ValidationResult.Invalid)
    }

    @Test
    fun `past deadline is invalid`() {
        val past = now.minus(2, ChronoUnit.DAYS)
        assertTrue(validator.validate(task("t", deadline = past), now = now) is ValidationResult.Invalid)
    }

    @Test
    fun `future deadline is valid`() {
        val future = now.plus(2, ChronoUnit.DAYS)
        assertTrue(validator.validate(task("t", deadline = future), now = now) is ValidationResult.Valid)
    }

    @Test
    fun `description over max length is invalid`() {
        val long = "x".repeat(TaskValidator.MAX_DESCRIPTION_LENGTH + 1)
        assertTrue(validator.validate(task("t", description = long), now = now) is ValidationResult.Invalid)
    }

    @Test
    fun `no cycle in acyclic graph`() {
        val edges = mapOf(1L to listOf(2L), 2L to listOf(3L), 3L to emptyList())
        assertFalse(validator.hasCircularDependency(edges))
    }

    @Test
    fun `cycle detected in cyclic graph`() {
        val edges = mapOf(1L to listOf(2L), 2L to listOf(3L), 3L to listOf(1L))
        assertTrue(validator.hasCircularDependency(edges))
    }

    @Test
    fun `self-loop is a cycle`() {
        val edges = mapOf(1L to listOf(1L))
        assertTrue(validator.hasCircularDependency(edges))
    }
}
