package com.taskmanager.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.taskmanager.data.local.entity.TagEntity
import com.taskmanager.data.local.entity.TaskEntity
import com.taskmanager.data.local.entity.TaskTagCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskTagDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(crossRef: TaskTagCrossRef): Long

    @Query("DELETE FROM task_tag_cross_ref WHERE taskId = :taskId AND tagId = :tagId")
    suspend fun delete(taskId: Long, tagId: Long)

    @Query("DELETE FROM task_tag_cross_ref WHERE taskId = :taskId")
    suspend fun deleteAllForTask(taskId: Long)

    @Query(
        """
        SELECT t.* FROM tags t
        INNER JOIN task_tag_cross_ref r ON r.tagId = t.id
        WHERE r.taskId = :taskId
        ORDER BY t.name
        """
    )
    fun getTagsForTask(taskId: Long): Flow<List<TagEntity>>

    @Query(
        """
        SELECT t.* FROM tags t
        INNER JOIN task_tag_cross_ref r ON r.tagId = t.id
        WHERE r.taskId = :taskId
        ORDER BY t.name
        """
    )
    suspend fun getTagsForTaskOnce(taskId: Long): List<TagEntity>

    @Query(
        """
        SELECT DISTINCT task.* FROM tasks task
        INNER JOIN task_tag_cross_ref r ON r.taskId = task.id
        INNER JOIN tags t ON t.id = r.tagId
        WHERE t.name = :tagName COLLATE NOCASE
        ORDER BY task.createdAt DESC
        """
    )
    fun getTasksByTag(tagName: String): Flow<List<TaskEntity>>

    @Query(
        """
        SELECT COUNT(*) FROM task_tag_cross_ref
        WHERE taskId = :taskId AND tagId = :tagId
        """
    )
    suspend fun relationExists(taskId: Long, tagId: Long): Int
}
