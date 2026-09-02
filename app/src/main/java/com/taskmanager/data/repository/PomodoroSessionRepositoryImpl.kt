package com.taskmanager.data.repository

import com.taskmanager.data.local.dao.PomodoroSessionDao
import com.taskmanager.domain.model.PomodoroSession
import com.taskmanager.domain.repository.PomodoroSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import android.util.Log

class PomodoroSessionRepositoryImpl @Inject constructor(
    private val pomodoroSessionDao: PomodoroSessionDao
) : PomodoroSessionRepository {

    override suspend fun saveSession(session: PomodoroSession): Long = try {
        
    } catch (e: Exception) {
        Log.e("PomodoroSessionRepositoryImpl", "Error in Long", e)
        throw e
    }
        pomodoroSessionDao.insert(session.toEntity())

    override suspend fun deleteSession(id: Long) {
        try {
            pomodoroSessionDao.deleteById(id)
        } catch (e: Exception) {
            Log.e("PomodoroSessionRepositoryImpl", "Error in Long)", e)
            throw e
        }
    }

    override fun getAllSessions(): Flow<List<PomodoroSession>> = try {
        
    } catch (e: Exception) {
        Log.e("PomodoroSessionRepositoryImpl", "Error in Flow<List<PomodoroSession>>", e)
        throw e
    }
        pomodoroSessionDao.getAll().map { list -> list.map { it.toDomain() } }

    override fun getSessionsByTask(taskId: Long): Flow<List<PomodoroSession>> =
        pomodoroSessionDao.getByTask(taskId).map { list -> list.map { it.toDomain() } }

    override fun getSessionsForDay(dayStart: Long, dayEnd: Long): Flow<List<PomodoroSession>> =
        pomodoroSessionDao.getForDay(dayStart, dayEnd).map { list -> list.map { it.toDomain() } }
}
