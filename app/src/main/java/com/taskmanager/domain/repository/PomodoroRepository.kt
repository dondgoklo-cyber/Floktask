package com.taskmanager.domain.repository

import com.taskmanager.domain.model.PomodoroSession
import com.taskmanager.domain.model.PomodoroType
import kotlinx.coroutines.flow.Flow

interface PomodoroRepository {
    suspend fun saveSession(
        taskId: Long?,
        durationMinutes: Int,
        type: PomodoroType
    ): Long

    suspend fun getSession(id: Long): PomodoroSession?
    fun getSessionsByTask(taskId: Long): Flow<List<PomodoroSession>>
    fun getAllSessions(): Flow<List<PomodoroSession>>
    fun getCompletedSessions(): Flow<List<PomodoroSession>>
    suspend fun deleteSession(id: Long)
}
