package com.taskmanager.domain.repository

import com.taskmanager.domain.model.Task
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun createTask(task: Task): Long
    suspend fun getTaskById(id: Long): Task?
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(id: Long)

    fun getAllTasks(): Flow<List<Task>>

    /** Paginated stream of all tasks (Paging3). */
    fun pagedTasks(pageSize: Int = 20): Flow<PagingData<Task>>
    fun getTasksByProject(projectId: Long): Flow<List<Task>>
    fun getCompletedTasks(): Flow<List<Task>>
    fun getIncompleteTasks(): Flow<List<Task>>
    fun searchTasks(query: String): Flow<List<Task>>
}
