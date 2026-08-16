package com.taskmanager.domain.usecase.batch

import com.taskmanager.domain.model.Task
import com.taskmanager.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class BatchTaskOperationsUseCaseTest {

    private val store = mutableMapOf<Long, Task>()

    private val fakeRepo = object : TaskRepository {
        override suspend fun createTask(task: Task): Long {
            val id = (store.keys.maxOrNull() ?: 0) + 1
            store[id] = task.copy(id = id)
            return id
        }

        override suspend fun getTaskById(id: Long): Task? = store[id]

        override suspend fun updateTask(task: Task) {
            val id = task.id ?: return
            store[id] = task
        }

        override suspend fun deleteTask(id: Long) {
            store.remove(id)
        }

        override fun getAllTasks(): Flow<List<Task>> = flowOf(store.values.toList())
        override fun getTasksByProject(projectId: Long): Flow<List<Task>> = flowOf(emptyList())
        override fun getCompletedTasks(): Flow<List<Task>> = flowOf(emptyList())
        override fun getIncompleteTasks(): Flow<List<Task>> = flowOf(emptyList())
        override fun searchTasks(query: String): Flow<List<Task>> = flowOf(emptyList())
    }

    private val useCase = BatchTaskOperationsUseCase(fakeRepo)

    private fun seed(n: Int): List<Long> {
        val ids = mutableListOf<Long>()
        repeat(n) { i ->
            val id = (store.keys.maxOrNull() ?: 0) + 1
            store[id] = Task(id = id, title = "t$i", createdAt = Instant.now(), updatedAt = Instant.now())
            ids.add(id)
        }
        return ids
    }

    @Test
    fun `complete marks all selected as completed`() {
        val ids = seed(3)
        val result = useCase.complete(ids, completed = true)
        assertEquals(3, result.affected)
        ids.forEach { id -> assertEquals(true, store[id]?.isCompleted) }
    }

    @Test
    fun `complete skips missing ids`() {
        val ids = seed(1) + listOf(999L)
        val result = useCase.complete(ids, completed = true)
        assertEquals(1, result.affected)
    }

    @Test
    fun `delete removes all selected`() {
        val ids = seed(3)
        val result = useCase.delete(ids)
        assertEquals(3, result.affected)
        ids.forEach { id -> assertEquals(null, store[id]) }
    }

    @Test
    fun `moveToProject sets projectId on all selected`() {
        val ids = seed(2)
        useCase.moveToProject(ids, projectId = 42L)
        ids.forEach { id -> assertEquals(42L, store[id]?.projectId) }
    }

    @Test
    fun `empty list affects nothing`() {
        assertEquals(0, useCase.complete(emptyList(), true).affected)
        assertEquals(0, useCase.delete(emptyList()).affected)
        assertEquals(0, useCase.moveToProject(emptyList(), null).affected)
    }
}
