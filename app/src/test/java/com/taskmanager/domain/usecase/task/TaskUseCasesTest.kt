package com.taskmanager.domain.usecase.task

import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.repository.TaskRepository
import com.taskmanager.notification.AlarmScheduler
import com.taskmanager.test.TestLogger
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant

class TaskUseCasesTest {

    private val store = mutableMapOf<Long, Task>()
    private lateinit var logger: TestLogger
    private lateinit var alarmScheduler: AlarmScheduler

    @Before
    fun setup() {
        logger = TestLogger()
        alarmScheduler = mockk<AlarmScheduler>(relaxed = true)
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

        override suspend fun deleteTask(id: Long) {
            store.remove(id)
        }

        override suspend fun cancelReminder(taskId: Long) {
            // no-op for mock
        }

        override fun getAllTasks(): Flow<List<Task>> = flowOf(store.values.toList())
        override fun getTasksByProject(projectId: Long): Flow<List<Task>> = flowOf(emptyList())
        override fun getCompletedTasks(): Flow<List<Task>> = flowOf(emptyList())
        override fun getIncompleteTasks(): Flow<List<Task>> = flowOf(emptyList())
        override fun searchTasks(query: String): Flow<List<Task>> = flowOf(emptyList())
        override fun getInboxTasks(): Flow<List<Task>> = flowOf(emptyList())
        override suspend fun getTaskTags(taskId: Long): List<String> = emptyList()
    }

    @Test
    fun `createTask persists and returns id`() = runBlocking {
        val useCase = CreateTaskUseCase(fakeRepo, logger)
        val id = useCase(Task(title = "Buy milk", createdAt = Instant.now(), updatedAt = Instant.now()))
        assertEquals(1L, id)
        assertEquals("Buy milk", store[id]?.title)
    }

    @Test
    fun `getTaskById returns stored task`() = runBlocking {
        val id = fakeRepo.createTask(Task(title = "x", createdAt = Instant.now(), updatedAt = Instant.now()))
        val useCase = GetTaskByIdUseCase(fakeRepo, logger)
        val fetched = useCase(id)
        assertEquals("x", fetched?.title)
    }

    @Test
    fun `getTaskById returns null for missing`() = runBlocking {
        val useCase = GetTaskByIdUseCase(fakeRepo, logger)
        assertNull(useCase(999L))
    }

    @Test
    fun `getTaskById logs error on failure`() = runBlocking {
        val failingRepo = object : TaskRepository {
            override suspend fun getTaskById(id: Long): Task? = throw Exception("Test error")
            override suspend fun createTask(task: Task): Long = 0
            override suspend fun updateTask(task: Task) {}
            override suspend fun deleteTask(id: Long) {}
            override suspend fun cancelReminder(taskId: Long) {}
            override fun getAllTasks(): Flow<List<Task>> = flowOf(emptyList())
            override fun getTasksByProject(projectId: Long): Flow<List<Task>> = flowOf(emptyList())
            override fun getCompletedTasks(): Flow<List<Task>> = flowOf(emptyList())
            override fun getIncompleteTasks(): Flow<List<Task>> = flowOf(emptyList())
            override fun searchTasks(query: String): Flow<List<Task>> = flowOf(emptyList())
            override fun getInboxTasks(): Flow<List<Task>> = flowOf(emptyList())
            override suspend fun getTaskTags(taskId: Long): List<String> = emptyList()
        }
        val useCase = GetTaskByIdUseCase(failingRepo, logger)
        useCase(999L)
        assertTrue(logger.contains("Error in invoke"))
    }

    @Test
    fun `deleteTask removes the task`() = runBlocking {
        val alarmScheduler = mockk<AlarmScheduler>(relaxed = true)
        coEvery { alarmScheduler.cancelReminder(any()) } returns Unit
        
        val id = fakeRepo.createTask(Task(title = "y", createdAt = Instant.now(), updatedAt = Instant.now()))
        val useCase = DeleteTaskUseCase(fakeRepo, alarmScheduler, logger)
        useCase(id)
        assertNull(fakeRepo.getTaskById(id))
    }

    @Test
    fun `updateTask overwrites fields`() = runBlocking {
        val id = fakeRepo.createTask(Task(title = "old", createdAt = Instant.now(), updatedAt = Instant.now()))
        val useCase = UpdateTaskUseCase(fakeRepo, logger)
        useCase(Task(id = id, title = "new", createdAt = Instant.now(), updatedAt = Instant.now()))
        assertEquals("new", store[id]?.title)
    }

    @Test
    fun `getAllTasks emits the full list`() = runBlocking {
        fakeRepo.createTask(Task(title = "a", createdAt = Instant.now(), updatedAt = Instant.now()))
        fakeRepo.createTask(Task(title = "b", createdAt = Instant.now(), updatedAt = Instant.now()))
        val useCase = GetAllTasksUseCase(fakeRepo)
        val list = useCase().first()
        assertTrue(list.size == 2)
    }

}
