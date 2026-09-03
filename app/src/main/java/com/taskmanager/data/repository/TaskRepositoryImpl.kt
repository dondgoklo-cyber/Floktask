package com.taskmanager.data.repository

import androidx.room.Transaction
import com.taskmanager.data.local.dao.TaskDao
import com.taskmanager.data.local.dao.TaskTagDao
import com.taskmanager.data.local.dao.TagDao
import com.taskmanager.data.local.entity.TaskTagEntity
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject
import android.util.Log

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val taskTagDao: TaskTagDao,
    private val tagDao: TagDao
) : TaskRepository {

    override suspend fun createTask(task: Task): Long = try {
        taskDao.insert(task.toEntity())
    } catch (e: Exception) {
        Log.e("TaskRepositoryImpl", "Error in Long", e)
        throw e
    }

    override suspend fun getTaskById(id: Long): Task? = try {
        taskDao.getById(id)?.toDomain()
    } catch (e: Exception) {
        Log.e("TaskRepositoryImpl", "Error in Task?", e)
        throw e
    }

    override suspend fun updateTask(task: Task) {
        try {
            taskDao.update(task.copy(updatedAt = Instant.now()).toEntity())
        } catch (e: Exception) {
            Log.e("TaskRepositoryImpl", "Error in Task", e)
            throw e
        }
    }

    override suspend fun deleteTask(id: Long) {
        try {
            taskDao.deleteById(id)
        } catch (e: Exception) {
            Log.e("TaskRepositoryImpl", "Error in Long", e)
            throw e
        }
    }

    override suspend fun setCompleted(id: Long, completed: Boolean) {
        try {
            taskDao.setCompleted(id, completed, Instant.now().toEpochMilli())
        } catch (e: Exception) {
            Log.e("TaskRepositoryImpl", "Error in Boolean", e)
            throw e
        }
    }

    override suspend fun updateReminderDate(taskId: Long, reminderDate: Long?) {
        try {
            taskDao.updateReminderDate(taskId, reminderDate, Instant.now().toEpochMilli())
        } catch (e: Exception) {
            Log.e("TaskRepositoryImpl", "Error in updateReminderDate", e)
            throw e
        }
    }

    override suspend fun cancelReminder(taskId: Long) {
        try {
            taskDao.cancelReminder(taskId, Instant.now().toEpochMilli())
        } catch (e: Exception) {
            Log.e("TaskRepositoryImpl", "Error in cancelReminder", e)
            throw e
        }
    }

    override fun getAllTasks(): Flow<List<Task>> = try {
        taskDao.getAll().map { list -> list.map { it.toDomain() } }
    } catch (e: Exception) {
        Log.e("TaskRepositoryImpl", "Error in Flow<List<Task>>", e)
        throw e
    }

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

    @Transaction
    override suspend fun setTaskTags(taskId: Long, tagIds: List<Long>) {
        try {
            taskTagDao.deleteByTaskId(taskId)
            taskTagDao.insertAll(tagIds.map { TaskTagEntity(taskId = taskId, tagId = it) })
        } catch (e: Exception) {
            Log.e("TaskRepositoryImpl", "Error in List<Long>", e)
            throw e
        }
    }

    override suspend fun getTaskTags(taskId: Long): List<String> {
        val tagIds = try {
            taskTagDao.getTagIdsForTask(taskId)
        } catch (e: Exception) {
            Log.e("TaskRepositoryImpl", "Error in tagIds", e)
            throw e
        }
        return tagIds.mapNotNull { id -> tagDao.getById(id)?.name }
    }

    override fun getTasksByTag(tagId: Long): Flow<List<Task>> =
        taskTagDao.getTasksForTag(tagId).map { list -> list.map { it.toDomain() } }
}
