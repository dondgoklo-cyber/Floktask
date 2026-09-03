package com.taskmanager.data.repository

import com.taskmanager.data.local.dao.HabitLogDao
import com.taskmanager.domain.model.HabitLog
import com.taskmanager.domain.repository.HabitLogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import android.util.Log

class HabitLogRepositoryImpl @Inject constructor(
    private val habitLogDao: HabitLogDao
) : HabitLogRepository {

    override suspend fun logCompletion(habitId: Long, date: LocalDate, count: Int): Long {
        val entity = try {
            HabitLog(
                habitId = habitId,
                date = date,
                count = count
            ).toEntity()
        } catch (e: Exception) {
            Log.e("HabitLogRepositoryImpl", "Error in entity", e)
            throw e
        }
        return habitLogDao.insert(entity)
    }

    override suspend fun removeForDay(habitId: Long, date: LocalDate) {
        try {
            habitLogDao.deleteForDay(habitId, date.toEpochDay())
        } catch (e: Exception) {
            Log.e("HabitLogRepositoryImpl", "Error in LocalDate", e)
            throw e
        }
    }

    override fun observeByHabit(habitId: Long): Flow<List<HabitLog>> = try {
        habitLogDao.observeByHabit(habitId).map { list -> list.map { it.toDomain() } }
    } catch (e: Exception) {
        Log.e("HabitLogRepositoryImpl", "Error in Flow<List<HabitLog>>", e)
        throw e
    }

    override suspend fun getByHabit(habitId: Long): List<HabitLog> = try {
        habitLogDao.getByHabit(habitId).map { it.toDomain() }
    } catch (e: Exception) {
        Log.e("HabitLogRepositoryImpl", "Error in List<HabitLog>", e)
        throw e
    }

    override suspend fun getForDay(habitId: Long, date: LocalDate): HabitLog? = try {
        habitLogDao.getForDay(habitId, date.toEpochDay())?.toDomain()
    } catch (e: Exception) {
        Log.e("HabitLogRepositoryImpl", "Error in HabitLog?", e)
        throw e
    }
}
