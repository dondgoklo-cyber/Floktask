package com.taskmanager.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.taskmanager.data.local.entity.LocationReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationReminderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: LocationReminderEntity): Long

    @Query("SELECT * FROM location_reminders WHERE id = :id")
    suspend fun getById(id: Long): LocationReminderEntity?

    @Query("SELECT * FROM location_reminders WHERE isActive = 1")
    suspend fun getActive(): List<LocationReminderEntity>

    @Query("SELECT * FROM location_reminders")
    fun observeAll(): Flow<List<LocationReminderEntity>>

    @Query("SELECT * FROM location_reminders WHERE taskId = :taskId")
    fun observeByTask(taskId: Long): Flow<List<LocationReminderEntity>>

    @Query("UPDATE location_reminders SET isActive = :active WHERE id = :id")
    suspend fun setActive(id: Long, active: Boolean)

    @Query("DELETE FROM location_reminders WHERE id = :id")
    suspend fun deleteById(id: Long)
}
