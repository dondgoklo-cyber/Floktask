package com.taskmanager.domain.usecase.task

import com.taskmanager.domain.model.Task
import com.taskmanager.domain.notification.ReminderScheduler
import com.taskmanager.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test
import java.time.Instant

class TaskUseCasesTest {

    private val store = mutableMapOf<Long, Task>()
    private val cancelled = mutableListOf<Long>()
    private val scheduled = mutableListOf<Pair<Long, Long>>() // taskId -> triggerAtMillis

    private val fakeScheduler = object : ReminderScheduler {
        override fun scheduleReminder(taskId: Long, title: String, triggerAtMillis: Long) {
            scheduled.add(taskId to triggerAtMillis)
        }
        override fun cancelReminder(taskId: Long) { cancelled.add(taskId) }
        override fun rescheduleAllReminders() {}
    }

    private val fakeRepo = object : TaskRepository {
        private var nextId = 0L

        override suspend fun createTask(task: Task): Long {
            nextId++
            store[nextId] = task.copy(id = nextId)
            return nextId
        }

        override suspend fun getTaskById(id: Long): Task? = store[id]

        override suspend fun updateTask(task: Task) {
            val id = task.id ?: return
            store[id] = task
        }

        override suspend fun deleteTask(id: Long) { store.remove(id) }

        override suspend fun setCompleted(id: Long, completed: Boolean) {
            store[id]?.let { store[id] = it.copy(isCompleted = completed) }
        }

        override fun getAllTasks(): Flow<List<Task>> = flowOf(store.values.toList())
        override fun getTasksByProject(projectId: Long): Flow<List<Task>> = flowOf(emptyList())
        override fun getTasksBySubproject(subprojectId: Long): Flow<List<Task>> = flowOf(emptyList())
        override fun getAllTasksByProjectIncludingSubprojects(projectId: Long): Flow<List<Task>> = flowOf(emptyList())
        override fun getCompletedTasks(): Flow<List<Task>> = flowOf(emptyList())
        override fun getIncompleteTasks(): Flow<List<Task>> = flowOf(emptyList())
        override fun searchTasks(query: String): Flow<List<Task>> = flowOf(emptyList())
        override fun getTimedTasksForDay(dayStart: Long, dayEnd: Long): Flow<List<Task>> = flowOf(emptyList())
        override fun getTasksForDay(dayStart: Long, dayEnd: Long): Flow<List<Task>> = flowOf(emptyList())
        override fun getTasksByEisenhowerQuadrant(quadrantName: String): Flow<List<Task>> = flowOf(emptyList())
        override fun getInboxTasks(): Flow<List<Task>> = flowOf(emptyList())
        override fun getUpcomingTasks(fromEpoch: Long): Flow<List<Task>> = flowOf(emptyList())
        override suspend fun setTaskTags(taskId: Long, tagIds: List<Long>) {}
        override suspend fun getTaskTags(taskId: Long): List<String> = emptyList()
        override fun getTasksByTag(tagId: Long): Flow<List<Task>> = flowOf(emptyList())
    }

    private fun now() = Instant.now()

    @Test
    fun `createTask persists and returns id`() = runBlocking {
        val useCase = CreateTaskUseCase(fakeRepo)
        val id = useCase(Task(title = "Buy milk", createdAt = now(), updatedAt = now()))
        assertEquals(1L, id)
        assertEquals("Buy milk", store[id]?.title)
    }

    @Test
    fun `getTaskById returns stored task`() = runBlocking {
        val id = fakeRepo.createTask(Task(title = "x", createdAt = now(), updatedAt = now()))
        val fetched = fakeRepo.getTaskById(id)
        assertEquals("x", fetched?.title)
    }

    @Test
    fun `getTaskById returns null for missing`() = runBlocking {
        assertNull(fakeRepo.getTaskById(999L))
    }

    @Test
    fun `deleteTask removes the task and cancels reminder`() = runBlocking {
        val id = fakeRepo.createTask(Task(title = "y", createdAt = now(), updatedAt = now()))
        val useCase = DeleteTaskUseCase(fakeRepo, fakeScheduler)
        useCase(id)
        assertNull(fakeRepo.getTaskById(id))
        assertTrue("deleting a task must cancel its reminder", cancelled.contains(id))
    }

    @Test
    fun `updateTask overwrites fields`() = runBlocking {
        val id = fakeRepo.createTask(Task(title = "old", createdAt = now(), updatedAt = now()))
        val useCase = UpdateTaskUseCase(fakeRepo, fakeScheduler)
        useCase(Task(id = id, title = "new", createdAt = now(), updatedAt = now()))
        assertEquals("new", store[id]?.title)
    }

    @Test
    fun `getAllTasks emits the full list`() = runBlocking {
        fakeRepo.createTask(Task(title = "a", createdAt = now(), updatedAt = now()))
        fakeRepo.createTask(Task(title = "b", createdAt = now(), updatedAt = now()))
        val useCase = GetAllTasksUseCase(fakeRepo)
        val list = useCase().first()
        assertTrue(list.size == 2)
    }

    @Test
    fun `completing a task via SetTaskCompletedUseCase cancels its reminder`() = runBlocking {
        val id = fakeRepo.createTask(Task(title = "do", createdAt = now(), updatedAt = now()))
        val useCase = SetTaskCompletedUseCase(fakeRepo, fakeScheduler)
        useCase(id, completed = true)
        assertTrue(store[id]?.isCompleted == true)
        assertTrue("completion must cancel reminder", cancelled.contains(id))
    }

    @Test
    fun `reopening a task does not cancel reminder`() = runBlocking {
        val id = fakeRepo.createTask(Task(title = "do", isCompleted = true, createdAt = now(), updatedAt = now()))
        val useCase = SetTaskCompletedUseCase(fakeRepo, fakeScheduler)
        useCase(id, completed = false)
        assertFalse(store[id]?.isCompleted ?: true)
        assertFalse("reopening must not cancel reminder", cancelled.contains(id))
    }

    @Test
    fun `updateTask with future reminder schedules alarm`() = runBlocking {
        val id = fakeRepo.createTask(Task(title = "remind", createdAt = now(), updatedAt = now()))
        val future = Instant.now().plusSeconds(3600)
        val useCase = UpdateTaskUseCase(fakeRepo, fakeScheduler)
        useCase(Task(id = id, title = "remind", reminderDate = future, createdAt = now(), updatedAt = now()))
        assertTrue("future reminder must be scheduled", scheduled.any { it.first == id })
    }

    @Test
    fun `updateTask with past reminder cancels alarm`() = runBlocking {
        val id = fakeRepo.createTask(Task(title = "remind", createdAt = now(), updatedAt = now()))
        val past = Instant.now().minusSeconds(3600)
        val useCase = UpdateTaskUseCase(fakeRepo, fakeScheduler)
        useCase(Task(id = id, title = "remind", reminderDate = past, createdAt = now(), updatedAt = now()))
        assertTrue("past reminder must be cancelled", cancelled.contains(id))
    }
}
