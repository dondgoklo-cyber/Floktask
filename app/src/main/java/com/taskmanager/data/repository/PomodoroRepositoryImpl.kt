package com.taskmanager.data.repository

import com.taskmanager.data.local.dao.PomodoroSessionDao
import com.taskmanager.domain.model.PomodoroSession
import com.taskmanager.domain.model.PomodoroType
import com.taskmanager.domain.repository.PomodoroRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject

class PomodoroRepositoryImpl @Inject constructor(
    private val pomodoroSessionDao: PomodoroSessionDao
) : PomodoroRepository {

    override suspend fun saveSession(
        taskId: Long?,
        durationMinutes: Int,
        type: PomodoroType
    ): Long {
        val session = PomodoroSession(
            taskId = taskId,
            startTime = Instant.now(),
            durationMinutes = durationMinutes,
            isCompleted = true,
            type = type
        )
        return pomodoroSessionDao.insert(session.toEntity())
    }

    override suspend fun getSession(id: Long): PomodoroSession? =
        pomodoroSessionDao.getById(id)?.toDomain()

    override fun getSessionsByTask(taskId: Long): Flow<List<PomodoroSession>> =
        pomodoroSessionDao.getByTask(taskId).map { list -> list.map { it.toDomain() } }

    override fun getAllSessions(): Flow<List<PomodoroSession>> =
        pomodoroSessionDao.getAll().map { list -> list.map { it.toDomain() } }

    override fun getCompletedSessions(): Flow<List<PomodoroSession>> =
        pomodoroSessionDao.getCompleted().map { list -> list.map { it.toDomain() } }

    override suspend fun deleteSession(id: Long) {
        pomodoroSessionDao.deleteById(id)
    }
}
