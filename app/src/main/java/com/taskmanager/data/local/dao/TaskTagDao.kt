package com.taskmanager.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.taskmanager.data.local.entity.TaskTagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskTagDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(taskTag: TaskTagEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(taskTags: List<TaskTagEntity>)

    @Query("DELETE FROM task_tags WHERE taskId = :taskId")
    suspend fun deleteByTaskId(taskId: Long)

    @Query("DELETE FROM task_tags WHERE taskId = :taskId AND tagId = :tagId")
    suspend fun delete(taskId: Long, tagId: Long)

    @Query("SELECT * FROM task_tags")
    suspend fun getAll(): List<TaskTagEntity>

    @Query("SELECT tagId FROM task_tags WHERE taskId = :taskId")
    suspend fun getTagIdsForTask(taskId: Long): List<Long>

    @Query("""
        SELECT t.* FROM tags t
        INNER JOIN task_tags tt ON t.id = tt.tagId
        WHERE tt.taskId = :taskId
    """)
    fun getTagsForTask(taskId: Long): Flow<List<com.taskmanager.data.local.entity.TagEntity>>

    @Query("""
        SELECT t.* FROM tasks t
        INNER JOIN task_tags tt ON t.id = tt.taskId
        WHERE tt.tagId = :tagId
    """)
    fun getTasksForTag(tagId: Long): Flow<List<com.taskmanager.data.local.entity.TaskEntity>>
}
