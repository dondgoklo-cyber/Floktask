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
    } catch (e: Exception) {
        Log.e("HabitLogRepositoryImpl", "Error in entity", e)
        throw e
    }
            habitId = habitId,
            date = date,
            count = count
        ).toEntity()
        return habitLogDao.insert(entity)
    }

    override suspend fun removeForDay(habitId: Long, date: LocalDate) {
        try {
            habitLogDao.deleteForDay(habitId, date.toEpochDay())
        } catch (e: Exception) {
            Log.e("HabitLogRepositoryImpl", "Error in LocalDate)", e)
            throw e
        }
    }

    override fun observeByHabit(habitId: Long): Flow<List<HabitLog>> = try {
        
    } catch (e: Exception) {
        Log.e("HabitLogRepositoryImpl", "Error in Flow<List<HabitLog>>", e)
        throw e
    }
        habitLogDao.observeByHabit(habitId).map { list -> list.map { it.toDomain() } }

    override suspend fun getByHabit(habitId: Long): List<HabitLog> = try {
        
    } catch (e: Exception) {
        Log.e("HabitLogRepositoryImpl", "Error in List<HabitLog>", e)
        throw e
    }
        habitLogDao.getByHabit(habitId).map { it.toDomain() }

    override suspend fun getForDay(habitId: Long, date: LocalDate): HabitLog? = try {
        
    } catch (e: Exception) {
        Log.e("HabitLogRepositoryImpl", "Error in HabitLog?", e)
        throw e
    }
        habitLogDao.getForDay(habitId, date.toEpochDay())?.toDomain()
}
