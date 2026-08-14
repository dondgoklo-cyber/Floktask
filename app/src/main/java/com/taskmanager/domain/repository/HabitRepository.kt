package com.taskmanager.domain.repository

import com.taskmanager.domain.model.Habit
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    suspend fun createHabit(habit: Habit): Long
    suspend fun getHabitById(id: Long): Habit?
    suspend fun updateHabit(habit: Habit)
    suspend fun deleteHabit(id: Long)
    suspend fun archiveHabit(id: Long, archived: Boolean)

    fun getActiveHabits(): Flow<List<Habit>>
    fun getAllHabits(): Flow<List<Habit>>
    fun getArchivedHabits(): Flow<List<Habit>>
}
