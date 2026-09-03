package com.taskmanager.domain.usecase.activity

import com.taskmanager.domain.model.Priority
import com.taskmanager.domain.model.Task
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class RecordTaskChangeUseCaseTest {

    private val useCase = RecordTaskChangeUseCase()
    private val now = Instant.parse("2026-01-15T10:00:00Z")

    private fun task(
        id: Long = 1,
        title: String = "t",
        priority: Priority = Priority.NONE,
        completed: Boolean = false,
        deadline: Instant? = null,
        projectId: Long? = null
    ) = Task(
        id = id, title = title, priority = priority, isCompleted = completed,
        deadline = deadline, projectId = projectId, createdAt = now, updatedAt = now
    )

    @Test
    fun `creation emits a single CREATED event`() {
        val events = useCase(before = null, after = task(1), actor = "alice", now = now)
        assertEquals(1, events.size)
        assertEquals(com.taskmanager.domain.model.ActivityAction.CREATED, events[0].action)
    }

    @Test
    fun `deletion emits a single DELETED event`() {
        val events = useCase(before = task(1), after = null, actor = "alice", now = now)
        assertEquals(1, events.size)
        assertEquals(com.taskmanager.domain.model.ActivityAction.DELETED, events[0].action)
    }

    @Test
    fun `title change emits an UPDATED event with old-new`() {
        val events = useCase(before = task(1, "old"), after = task(1, "new"), now = now)
        assertEquals(1, events.size)
        assertEquals("title", events[0].field)
        assertEquals("old", events[0].oldValue)
        assertEquals("new", events[0].newValue)
    }

    @Test
    fun `completion emits COMPLETED action`() {
        val events = useCase(before = task(1, completed = false), after = task(1, completed = true), now = now)
        assertEquals(1, events.size)
        assertEquals(com.taskmanager.domain.model.ActivityAction.COMPLETED, events[0].action)
    }

    @Test
    fun `multiple field changes emit multiple events`() {
        val before = task(1, title = "a", priority = Priority.LOW, deadline = now)
        val after = task(1, title = "b", priority = Priority.HIGH, deadline = now.plusSeconds(3600))
        val events = useCase(before, after, now = now)
        assertEquals(3, events.size) // title, priority, deadline
    }

    @Test
    fun `no changes emits nothing`() {
        val t = task(1, "same")
        assertTrue(useCase(t, t, now = now).isEmpty())
    }

    @Test
    fun `describe produces a readable sentence`() {
        val event = useCase(before = task(1, "old"), after = task(1, "new"), actor = "bob", now = now)[0]
        val text = event.describe()
        assertTrue(text.contains("bob"))
        assertTrue(text.contains("updated"))
        assertTrue(text.contains("title"))
        assertTrue(text.contains("old"))
        assertTrue(text.contains("new"))
    }
}
