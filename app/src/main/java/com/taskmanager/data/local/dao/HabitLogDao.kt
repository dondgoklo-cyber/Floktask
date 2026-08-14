package com.taskmanager.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.taskmanager.data.local.entity.HabitLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: HabitLogEntity): Long

    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId ORDER BY date ASC")
    fun observeByHabit(habitId: Long): Flow<List<HabitLogEntity>>

    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId ORDER BY date ASC")
    suspend fun getByHabit(habitId: Long): List<HabitLogEntity>

    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId AND date = :epochDay")
    suspend fun getForDay(habitId: Long, epochDay: Long): HabitLogEntity?

    @Query("DELETE FROM habit_logs WHERE habitId = :habitId AND date = :epochDay")
    suspend fun deleteForDay(habitId: Long, epochDay: Long)

    @Query("DELETE FROM habit_logs WHERE habitId = :habitId")
    suspend fun deleteAllForHabit(habitId: Long)
}
