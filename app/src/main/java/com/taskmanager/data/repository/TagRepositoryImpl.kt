package com.taskmanager.data.repository

import com.taskmanager.data.local.dao.TagDao
import com.taskmanager.domain.model.Tag
import com.taskmanager.domain.repository.TagRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TagRepositoryImpl @Inject constructor(
    private val tagDao: TagDao
) : TagRepository {

    override suspend fun createTag(tag: Tag): Long =
        tagDao.insert(tag.toEntity())

    override suspend fun deleteTag(id: Long) {
        tagDao.deleteById(id)
    }

    override fun getAllTags(): Flow<List<Tag>> =
        tagDao.getAll().map { list -> list.map { it.toDomain() } }
}
