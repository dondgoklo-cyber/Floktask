package com.taskmanager.data.repository

import com.taskmanager.data.local.dao.PomodoroSessionDao
import com.taskmanager.domain.model.PomodoroSession
import com.taskmanager.domain.repository.PomodoroSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PomodoroSessionRepositoryImpl @Inject constructor(
    private val pomodoroSessionDao: PomodoroSessionDao
) : PomodoroSessionRepository {

    override suspend fun saveSession(session: PomodoroSession): Long =
        pomodoroSessionDao.insert(session.toEntity())

    override suspend fun deleteSession(id: Long) {
        pomodoroSessionDao.deleteById(id)
    }

    override fun getAllSessions(): Flow<List<PomodoroSession>> =
        pomodoroSessionDao.getAll().map { list -> list.map { it.toDomain() } }

    override fun getSessionsByTask(taskId: Long): Flow<List<PomodoroSession>> =
        pomodoroSessionDao.getByTask(taskId).map { list -> list.map { it.toDomain() } }

    override fun getSessionsForDay(dayStart: Long, dayEnd: Long): Flow<List<PomodoroSession>> =
        pomodoroSessionDao.getForDay(dayStart, dayEnd).map { list -> list.map { it.toDomain() } }
}
