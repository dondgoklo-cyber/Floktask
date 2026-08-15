package com.taskmanager.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.taskmanager.data.local.entity.GoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: GoalEntity): Long

    @Update
    suspend fun update(goal: GoalEntity)

    @Query("SELECT * FROM goals ORDER BY createdAt DESC")
    fun getAll(): Flow<List<GoalEntity>>

    @Query("DELETE FROM goals WHERE id = :id")
    suspend fun deleteById(id: Long)
}
