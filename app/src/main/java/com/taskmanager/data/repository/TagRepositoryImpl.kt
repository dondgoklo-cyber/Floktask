package com.taskmanager.data.repository

import com.taskmanager.data.local.dao.TagDao
import com.taskmanager.domain.model.Tag
import com.taskmanager.domain.repository.TagRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import android.util.Log

class TagRepositoryImpl @Inject constructor(
    private val tagDao: TagDao
) : TagRepository {

    override suspend fun createTag(tag: Tag): Long = try {
        tagDao.insert(tag.toEntity())
    } catch (e: Exception) {
        Log.e("TagRepositoryImpl", "Error in Long", e)
        throw e
    }

    override suspend fun updateTag(tag: Tag) {
        try {
            tagDao.update(tag.toEntity())
        } catch (e: Exception) {
            Log.e("TagRepositoryImpl", "Error in Tag", e)
            throw e
        }
    }

    override suspend fun deleteTag(id: Long) {
        try {
            tagDao.deleteById(id)
        } catch (e: Exception) {
            Log.e("TagRepositoryImpl", "Error in Long", e)
            throw e
        }
    }

    override fun getAllTags(): Flow<List<Tag>> = try {
        tagDao.getAll().map { list -> list.map { it.toDomain() } }
    } catch (e: Exception) {
        Log.e("TagRepositoryImpl", "Error in Flow<List<Tag>>", e)
        throw e
    }
}
