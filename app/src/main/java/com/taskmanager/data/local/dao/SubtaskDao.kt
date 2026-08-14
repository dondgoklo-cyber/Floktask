package com.taskmanager.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.taskmanager.data.local.entity.SubtaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubtaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(subtask: SubtaskEntity): Long

    @Update
    suspend fun update(subtask: SubtaskEntity)

    @Delete
    suspend fun delete(subtask: SubtaskEntity)

    @Query("SELECT * FROM subtasks WHERE taskId = :taskId ORDER BY orderIndex ASC, id ASC")
    fun getByTask(taskId: Long): Flow<List<SubtaskEntity>>

    @Query("SELECT * FROM subtasks WHERE taskId = :taskId ORDER BY parentSubtaskId IS NOT NULL, parentSubtaskId ASC, orderIndex ASC, id ASC")
    suspend fun getListByTask(taskId: Long): List<SubtaskEntity>

    @Query("DELETE FROM subtasks WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE subtasks SET isCompleted = :completed WHERE id = :id")
    suspend fun setCompleted(id: Long, completed: Boolean)
}
