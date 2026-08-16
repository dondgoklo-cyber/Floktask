package com.taskmanager.data.local.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.taskmanager.data.local.entity.TimeBlockEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TimeBlockDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(block: TimeBlockEntity): Long

    @Update
    suspend fun update(block: TimeBlockEntity)

    @Query("SELECT * FROM time_blocks WHERE id = :id")
    suspend fun getById(id: Long): TimeBlockEntity?

    @Query("DELETE FROM time_blocks WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM time_blocks ORDER BY startTime ASC")
    fun getAll(): Flow<List<TimeBlockEntity>>

    @Query(
        """
        SELECT * FROM time_blocks
        WHERE startTime >= :dayStartMillis
          AND startTime < :dayEndMillis
        ORDER BY startTime ASC
        """
    )
    fun getByDayRange(dayStartMillis: Long, dayEndMillis: Long): Flow<List<TimeBlockEntity>>

    @Query("SELECT * FROM time_blocks WHERE taskId = :taskId ORDER BY startTime ASC")
    fun getByTask(taskId: Long): Flow<List<TimeBlockEntity>>

    @Query("SELECT * FROM time_blocks WHERE projectId = :projectId ORDER BY startTime ASC")
    fun getByProject(projectId: Long): Flow<List<TimeBlockEntity>>

    @Query(
        """
        SELECT * FROM time_blocks
        WHERE startTime < :endEpochMillis
          AND endTime > :startEpochMillis
        ORDER BY startTime ASC
        """
    )
    fun getBetween(startEpochMillis: Long, endEpochMillis: Long): Flow<List<TimeBlockEntity>>

    @Query("UPDATE time_blocks SET isCompleted = :completed, updatedAt = :updatedAt WHERE id = :id")
    suspend fun setCompleted(id: Long, completed: Boolean, updatedAt: Long)
}
