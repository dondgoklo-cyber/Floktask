package com.taskmanager.data.repository

import com.taskmanager.data.local.dao.TaskDao
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {

    override suspend fun createTask(task: Task): Long =
        taskDao.insert(task.toEntity())

    override suspend fun getTaskById(id: Long): Task? =
        taskDao.getById(id)?.toDomain()

    override suspend fun updateTask(task: Task) {
        taskDao.update(task.copy(updatedAt = Instant.now()).toEntity())
    }

    override suspend fun deleteTask(id: Long) {
        taskDao.deleteById(id)
    }

    override suspend fun setCompleted(id: Long, completed: Boolean) {
        taskDao.setCompleted(id, completed, Instant.now().toEpochMilli())
    }

    override fun getAllTasks(): Flow<List<Task>> =
        taskDao.getAll().map { list -> list.map { it.toDomain() } }

    override fun getTasksByProject(projectId: Long): Flow<List<Task>> =
        taskDao.getByProject(projectId).map { list -> list.map { it.toDomain() } }

    override fun getCompletedTasks(): Flow<List<Task>> =
        taskDao.getCompletedTasks().map { list -> list.map { it.toDomain() } }

    override fun getIncompleteTasks(): Flow<List<Task>> =
        taskDao.getIncompleteTasks().map { list -> list.map { it.toDomain() } }

    override fun searchTasks(query: String): Flow<List<Task>> =
        taskDao.search("%$query%").map { list -> list.map { it.toDomain() } }

    override fun getTimedTasksForDay(dayStart: Long, dayEnd: Long): Flow<List<Task>> =
        taskDao.getTimedTasksForDay(dayStart, dayEnd).map { list -> list.map { it.toDomain() } }

    override fun getTasksForDay(dayStart: Long, dayEnd: Long): Flow<List<Task>> =
        taskDao.getTasksForDay(dayStart, dayEnd).map { list -> list.map { it.toDomain() } }

    override fun getTasksByEisenhowerQuadrant(quadrantName: String): Flow<List<Task>> =
        taskDao.getTasksByQuadrant(quadrantName).map { list -> list.map { it.toDomain() } }

    override fun getInboxTasks(): Flow<List<Task>> =
        taskDao.getInboxTasks().map { list -> list.map { it.toDomain() } }

    override fun getUpcomingTasks(fromEpoch: Long): Flow<List<Task>> =
        taskDao.getUpcomingTasks(fromEpoch).map { list -> list.map { it.toDomain() } }
}
