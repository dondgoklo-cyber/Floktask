package com.taskmanager.domain.repository

import com.taskmanager.domain.model.HabitLog
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface HabitLogRepository {
    suspend fun logCompletion(habitId: Long, date: LocalDate, count: Int = 1): Long
    suspend fun removeForDay(habitId: Long, date: LocalDate)

    fun observeByHabit(habitId: Long): Flow<List<HabitLog>>
    suspend fun getByHabit(habitId: Long): List<HabitLog>
    suspend fun getForDay(habitId: Long, date: LocalDate): HabitLog?
}
