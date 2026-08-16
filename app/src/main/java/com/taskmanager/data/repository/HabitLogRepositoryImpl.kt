package com.taskmanager.data.repository

import com.taskmanager.data.local.dao.HabitLogDao
import com.taskmanager.domain.model.HabitLog
import com.taskmanager.domain.repository.HabitLogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class HabitLogRepositoryImpl @Inject constructor(
    private val habitLogDao: HabitLogDao
) : HabitLogRepository {

    override suspend fun logCompletion(habitId: Long, date: LocalDate, count: Int): Long {
        val entity = HabitLog(
            habitId = habitId,
            date = date,
            count = count
        ).toEntity()
        return habitLogDao.insert(entity)
    }

    override suspend fun removeForDay(habitId: Long, date: LocalDate) {
        habitLogDao.deleteForDay(habitId, date.toEpochDay())
    }

    override fun observeByHabit(habitId: Long): Flow<List<HabitLog>> =
        habitLogDao.observeByHabit(habitId).map { list -> list.map { it.toDomain() } }

    override suspend fun getByHabit(habitId: Long): List<HabitLog> =
        habitLogDao.getByHabit(habitId).map { it.toDomain() }

    override suspend fun getForDay(habitId: Long, date: LocalDate): HabitLog? =
        habitLogDao.getForDay(habitId, date.toEpochDay())?.toDomain()
}
