package com.taskmanager.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.taskmanager.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity): Long

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getById(id: Long): TaskEntity?

    @Query("SELECT * FROM tasks")
    fun getAll(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE projectId = :projectId")
    fun getByProject(projectId: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0")
    fun getIncompleteTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 1")
    fun getCompletedTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE title LIKE :query OR description LIKE :query")
    fun search(query: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE deadline IS NOT NULL ORDER BY deadline ASC")
    fun getWithDeadline(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE deadline >= :startMillis AND deadline < :endMillis ORDER BY deadline ASC")
    fun getByDeadlineBetween(startMillis: Long, endMillis: Long): Flow<List<TaskEntity>>

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE tasks SET isCompleted = :completed, updatedAt = :updatedAt WHERE id = :id")
    suspend fun setCompleted(id: Long, completed: Boolean, updatedAt: Long)
}
