package com.taskmanager.data.repository
import com.taskmanager.domain.logger.Logger

import com.taskmanager.data.local.dao.PomodoroSessionDao
import com.taskmanager.domain.model.PomodoroSession
import com.taskmanager.domain.repository.PomodoroSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PomodoroSessionRepositoryImpl @Inject constructor(
    private val logger: Logger,
    private val pomodoroSessionDao: PomodoroSessionDao
) : PomodoroSessionRepository {

    override suspend fun saveSession(session: PomodoroSession): Long = try {
        pomodoroSessionDao.insert(session.toEntity())
    } catch (e: Exception) {
        logger.error("PomodoroSessionRepositoryImpl", "Error in saveSession", e)
        throw e
    }

    override suspend fun deleteSession(id: Long) = try {
        pomodoroSessionDao.deleteById(id)
    } catch (e: Exception) {
        logger.error("PomodoroSessionRepositoryImpl", "Error in deleteSession", e)
        throw e
    }

    override fun getAllSessions(): Flow<List<PomodoroSession>> = try {
        pomodoroSessionDao.getAll().map { list -> list.map { it.toDomain() } }
    } catch (e: Exception) {
        logger.error("PomodoroSessionRepositoryImpl", "Error in getAllSessions", e)
        throw e
    }

    override fun getSessionsByTask(taskId: Long): Flow<List<PomodoroSession>> =
        pomodoroSessionDao.getByTask(taskId).map { list -> list.map { it.toDomain() } }

    override fun getSessionsForDay(dayStart: Long, dayEnd: Long): Flow<List<PomodoroSession>> =
        pomodoroSessionDao.getForDay(dayStart, dayEnd).map { list -> list.map { it.toDomain() } }
}
