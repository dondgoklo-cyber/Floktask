package com.taskmanager.data.repository

import com.taskmanager.data.local.dao.TaskDao
import com.taskmanager.data.local.notification.LocalNotificationManager
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.util.Date
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val notificationManager: LocalNotificationManager
) : TaskRepository {

    override suspend fun createTask(task: Task): Long {
        val entityId = taskDao.insert(task.toEntity())
        // Планируем уведомление для новой задачи с дедлайном
        task.copy(id = entityId).let { notificationManager.scheduleTaskReminder(it) }
        return entityId
    }

    override suspend fun getTaskById(id: Long): Task? =
        taskDao.getById(id)?.toDomain()

    override suspend fun updateTask(task: Task) {
        val updatedTask = task.copy(updatedAt = Instant.now())
        taskDao.update(updatedTask.toEntity())
        // Перепланируем уведомление при обновлении задачи
        notificationManager.scheduleTaskReminder(updatedTask)
    }

    override suspend fun deleteTask(id: Long) {
        // Отменяем уведомление перед удалением
        notificationManager.cancelTaskReminder(id)
        taskDao.deleteById(id)
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
}
