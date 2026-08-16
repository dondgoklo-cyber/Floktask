package com.taskmanager.domain.repository

import com.taskmanager.domain.model.PomodoroSession
import kotlinx.coroutines.flow.Flow

interface PomodoroSessionRepository {
    suspend fun saveSession(session: PomodoroSession): Long
    suspend fun deleteSession(id: Long)

    fun getAllSessions(): Flow<List<PomodoroSession>>
    fun getSessionsByTask(taskId: Long): Flow<List<PomodoroSession>>
    fun getSessionsForDay(dayStart: Long, dayEnd: Long): Flow<List<PomodoroSession>>
}
