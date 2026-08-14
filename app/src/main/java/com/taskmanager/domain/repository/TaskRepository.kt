package com.taskmanager.domain.repository

import com.taskmanager.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun createTask(task: Task): Long
    suspend fun getTaskById(id: Long): Task?
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(id: Long)

    fun getAllTasks(): Flow<List<Task>>
    fun getTasksByProject(projectId: Long): Flow<List<Task>>
    fun getCompletedTasks(): Flow<List<Task>>
    fun getIncompleteTasks(): Flow<List<Task>>
    fun searchTasks(query: String): Flow<List<Task>>
}
