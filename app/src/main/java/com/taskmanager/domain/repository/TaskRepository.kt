package com.taskmanager.domain.repository

import com.taskmanager.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun createTask(task: Task): Long
    suspend fun getTaskById(id: Long): Task?
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(id: Long)
    suspend fun setCompleted(id: Long, completed: Boolean)

    fun getAllTasks(): Flow<List<Task>>
    fun getTasksByProject(projectId: Long): Flow<List<Task>>
    fun getCompletedTasks(): Flow<List<Task>>
    fun getIncompleteTasks(): Flow<List<Task>>
    fun searchTasks(query: String): Flow<List<Task>>

    // Tag management (many-to-many)
    suspend fun setTaskTags(taskId: Long, tagIds: List<Long>)
    suspend fun getTaskTags(taskId: Long): List<String>
    fun getTasksByTag(tagId: Long): Flow<List<Task>>

    /** Задачи с запланированным временем (time blocks) за день [dayStart, dayEnd). */
    fun getTimedTasksForDay(dayStart: Long, dayEnd: Long): Flow<List<Task>>

    /** Все задачи дня (deadline или startTime в диапазоне). */
    fun getTasksForDay(dayStart: Long, dayEnd: Long): Flow<List<Task>>

    /** Задачи по квадранту Эйзенхауэра. */
    fun getTasksByEisenhowerQuadrant(quadrantName: String): Flow<List<Task>>

    /** Inbox: невыполненные задачи без проекта и без даты (быстрый захват). */
    fun getInboxTasks(): Flow<List<Task>>

    /** Upcoming: невыполненные задачи с дедлайном/startTime в будущем. */
    fun getUpcomingTasks(fromEpoch: Long): Flow<List<Task>>
}
