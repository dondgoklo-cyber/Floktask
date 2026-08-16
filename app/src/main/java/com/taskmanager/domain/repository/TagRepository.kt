package com.taskmanager.domain.repository

import com.taskmanager.domain.model.Tag
import com.taskmanager.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TagRepository {
    suspend fun createTag(tag: Tag): Long
    suspend fun deleteTag(id: Long)

    fun getAllTags(): Flow<List<Tag>>

    /** Tags currently attached to a task. */
    fun getTagsForTask(taskId: Long): Flow<List<Tag>>

    /**
     * Replace the set of tags on [taskId] with [tagNames], creating any tags
     * that don't yet exist. Returns the resulting tag ids.
     */
    suspend fun setTagsForTask(taskId: Long, tagNames: List<String>): List<Long>

    /** Add a single tag to a task (no-op if already attached). */
    suspend fun addTagToTask(taskId: Long, tagName: String): Long

    /** Remove a tag from a task (no-op if not attached). */
    suspend fun removeTagFromTask(taskId: Long, tagName: String)

    /** Tasks that have [tagName] attached. */
    fun getTasksByTag(tagName: String): Flow<List<Task>>
}
