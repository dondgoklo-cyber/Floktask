package com.taskmanager.data.repository

import com.taskmanager.data.local.dao.TimeBlockDao
import com.taskmanager.domain.model.TimeBlock
import com.taskmanager.domain.repository.TimeBlockRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class TimeBlockRepositoryImpl @Inject constructor(
    private val timeBlockDao: TimeBlockDao
) : TimeBlockRepository {

    private val zone: ZoneId = ZoneId.systemDefault()

    override suspend fun createTimeBlock(block: TimeBlock): Long =
        timeBlockDao.insert(block.toEntity())

    override suspend fun getTimeBlock(id: Long): TimeBlock? =
        timeBlockDao.getById(id)?.toDomain()

    override suspend fun updateTimeBlock(block: TimeBlock) {
        timeBlockDao.update(block.toEntity())
    }

    override suspend fun deleteTimeBlock(id: Long) {
        timeBlockDao.deleteById(id)
    }

    override fun getAllTimeBlocks(): Flow<List<TimeBlock>> =
        timeBlockDao.getAll().map { list -> list.map { it.toDomain() } }

    override fun getTimeBlocksByDate(date: LocalDate): Flow<List<TimeBlock>> {
        val dayStart = date.atStartOfDay(zone).toInstant().toEpochMilli()
        val dayEnd = date.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
        return timeBlockDao.getByDayRange(dayStart, dayEnd)
            .map { list -> list.map { it.toDomain() } }
    }

    override fun getTimeBlocksByTask(taskId: Long): Flow<List<TimeBlock>> =
        timeBlockDao.getByTask(taskId).map { list -> list.map { it.toDomain() } }

    override fun getTimeBlocksByProject(projectId: Long): Flow<List<TimeBlock>> =
        timeBlockDao.getByProject(projectId).map { list -> list.map { it.toDomain() } }

    override fun getTimeBlocksBetween(
        startEpochMillis: Long,
        endEpochMillis: Long
    ): Flow<List<TimeBlock>> =
        timeBlockDao.getBetween(startEpochMillis, endEpochMillis)
            .map { list -> list.map { it.toDomain() } }
}
