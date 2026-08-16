package com.taskmanager.domain.repository

import com.taskmanager.domain.model.Tag
import kotlinx.coroutines.flow.Flow

interface TagRepository {
    suspend fun createTag(tag: Tag): Long
    suspend fun updateTag(tag: Tag)
    suspend fun deleteTag(id: Long)

    fun getAllTags(): Flow<List<Tag>>
}
