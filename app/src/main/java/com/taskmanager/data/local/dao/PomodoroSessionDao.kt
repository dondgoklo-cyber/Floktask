package com.taskmanager.data.local.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.taskmanager.data.local.entity.PomodoroSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PomodoroSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: PomodoroSessionEntity): Long

    @Query("SELECT * FROM pomodoro_sessions WHERE id = :id")
    suspend fun getById(id: Long): PomodoroSessionEntity?

    @Query("SELECT * FROM pomodoro_sessions WHERE taskId = :taskId ORDER BY startTime DESC")
    fun getByTask(taskId: Long): Flow<List<PomodoroSessionEntity>>

    @Query("SELECT * FROM pomodoro_sessions ORDER BY startTime DESC")
    fun getAll(): Flow<List<PomodoroSessionEntity>>

    @Query("SELECT * FROM pomodoro_sessions WHERE isCompleted = 1 ORDER BY startTime DESC")
    fun getCompleted(): Flow<List<PomodoroSessionEntity>>

    @Query("DELETE FROM pomodoro_sessions WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COALESCE(SUM(durationMinutes), 0) FROM pomodoro_sessions WHERE taskId = :taskId AND type = 'WORK' AND isCompleted = 1")
    fun focusMinutesForTask(taskId: Long): Flow<Int>

    @Query("SELECT COALESCE(SUM(durationMinutes), 0) FROM pomodoro_sessions WHERE type = 'WORK' AND isCompleted = 1")
    fun totalFocusMinutes(): Flow<Int>
}
