package com.taskmanager.domain.usecase.eisenhower

import com.taskmanager.domain.model.EisenhowerQuadrant
import com.taskmanager.domain.model.Priority
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.nlp.EisenhowerClassifier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class EisenhowerClassifierTest {

    private val zone = ZoneId.systemDefault()

    private fun classifier(now: Instant) =
        EisenhowerClassifier(zone = zone, nowProvider = { now })

    @Test
    fun `urgent and important task is Q1`() {
        val now = Instant.parse("2026-01-15T10:00:00Z")
        val task = Task(
            title = "Bug",
            priority = Priority.HIGH,
            deadline = now.plus(2, ChronoUnit.HOURS)
        )
        assertEquals(EisenhowerQuadrant.Q1, classifier(now).classify(task).quadrant)
    }

    @Test
    fun `important not urgent is Q2`() {
        val now = Instant.parse("2026-01-15T10:00:00Z")
        val task = Task(
            title = "Plan",
            priority = Priority.HIGH,
            deadline = now.plus(7, ChronoUnit.DAYS)
        )
        assertEquals(EisenhowerQuadrant.Q2, classifier(now).classify(task).quadrant)
    }

    @Test
    fun `urgent not important is Q3`() {
        val now = Instant.parse("2026-01-15T10:00:00Z")
        val task = Task(
            title = "Email",
            priority = Priority.LOW,
            deadline = now.plus(1, ChronoUnit.HOURS)
        )
        assertEquals(EisenhowerQuadrant.Q3, classifier(now).classify(task).quadrant)
    }

    @Test
    fun `not urgent not important is Q4`() {
        val now = Instant.parse("2026-01-15T10:00:00Z")
        val task = Task(
            title = "Browse",
            priority = Priority.LOW,
            deadline = now.plus(30, ChronoUnit.DAYS)
        )
        assertEquals(EisenhowerQuadrant.Q4, classifier(now).classify(task).quadrant)
    }

    @Test
    fun `no deadline never urgent`() {
        val now = Instant.parse("2026-01-15T10:00:00Z")
        val task = Task(title = "Someday", priority = Priority.HIGH)
        assertEquals(EisenhowerQuadrant.Q2, classifier(now).classify(task).quadrant)
    }

    @Test
    fun `completed task is never urgent`() {
        val now = Instant.parse("2026-01-15T10:00:00Z")
        val task = Task(
            title = "Done",
            priority = Priority.HIGH,
            deadline = now.minus(1, ChronoUnit.HOURS),
            isCompleted = true
        )
        assertEquals(EisenhowerQuadrant.Q2, classifier(now).classify(task).quadrant)
    }
}

class DistributeTasksByEisenhowerUseCaseTest {

    private val zone = ZoneId.systemDefault()
    private val now = Instant.parse("2026-01-15T10:00:00Z")
    private val useCase = DistributeTasksByEisenhowerUseCase(EisenhowerClassifier(zone) { now })

    @Test
    fun `buckets tasks by quadrant and excludes completed`() {
        val q1 = Task(title = "Bug", priority = Priority.HIGH, deadline = now.plusSeconds(3600))
        val q2 = Task(title = "Plan", priority = Priority.HIGH, deadline = now.plusSeconds(86400 * 7))
        val q3 = Task(title = "Email", priority = Priority.LOW, deadline = now.plusSeconds(3600))
        val q4 = Task(title = "Browse", priority = Priority.LOW, deadline = now.plusSeconds(86400 * 30))
        val done = Task(title = "Done", priority = Priority.HIGH, deadline = now.plusSeconds(3600), isCompleted = true)

        val buckets = useCase(listOf(q1, q2, q3, q4, done))

        assertEquals(listOf(q1), buckets[EisenhowerQuadrant.Q1])
        assertEquals(listOf(q2), buckets[EisenhowerQuadrant.Q2])
        assertEquals(listOf(q3), buckets[EisenhowerQuadrant.Q3])
        assertEquals(listOf(q4), buckets[EisenhowerQuadrant.Q4])
        assertEquals(0, buckets.values.sumOf { it.count { t -> t.isCompleted } })
    }

    @Test
    fun `Q1 sorted by deadline ascending`() {
        val earlier = Task(title = "A", priority = Priority.HIGH, deadline = now.plusSeconds(60))
        val later = Task(title = "B", priority = Priority.HIGH, deadline = now.plusSeconds(3600))
        val buckets = useCase(listOf(later, earlier))
        assertEquals(listOf(earlier, later), buckets[EisenhowerQuadrant.Q1])
    }
}

class GetSmartAssistantSuggestionUseCaseTest {

    private val zone = ZoneId.systemDefault()
    private val now = Instant.parse("2026-01-15T10:00:00Z")
    private val useCase = GetSmartAssistantSuggestionUseCase(EisenhowerClassifier(zone) { now })

    @Test
    fun `suggests Q1 task first`() {
        val q1 = Task(title = "Bug", priority = Priority.HIGH, deadline = now.plusSeconds(3600))
        val q2 = Task(title = "Plan", priority = Priority.HIGH, deadline = now.plusSeconds(86400 * 7))
        val suggestion = useCase(listOf(q2, q1))
        assertEquals(q1, suggestion.nextTask)
        assertTrue(suggestion.message.contains("Q1"))
    }

    @Test
    fun `all done returns congratulatory message`() {
        val done = Task(title = "Done", priority = Priority.HIGH, isCompleted = true)
        val suggestion = useCase(listOf(done))
        assertNull(suggestion.nextTask)
        assertNotNull(suggestion.message)
    }
}
