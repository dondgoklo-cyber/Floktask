package com.taskmanager.data.repository

import com.taskmanager.data.local.dao.TaskDao
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.repository.TaskRepository
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
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

    override fun getAllTasks(): Flow<List<Task>> =
        taskDao.getAll().map { list -> list.map { it.toDomain() } }

    override fun pagedTasks(pageSize: Int): Flow<PagingData<Task>> =
        Pager(
            config = PagingConfig(pageSize = pageSize, prefetchDistance = pageSize, enablePlaceholders = false),
            pagingSourceFactory = { taskDao.pagingSource() }
        ).flow.map { pagingData -> pagingData.map { it.toDomain() } }

    override fun getTasksByProject(projectId: Long): Flow<List<Task>> =
        taskDao.getByProject(projectId).map { list -> list.map { it.toDomain() } }

    override fun getCompletedTasks(): Flow<List<Task>> =
        taskDao.getCompletedTasks().map { list -> list.map { it.toDomain() } }

    override fun getIncompleteTasks(): Flow<List<Task>> =
        taskDao.getIncompleteTasks().map { list -> list.map { it.toDomain() } }

    override fun searchTasks(query: String): Flow<List<Task>> =
        taskDao.search("%$query%").map { list -> list.map { it.toDomain() } }
}
