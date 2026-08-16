package com.taskmanager.data.repository

import com.taskmanager.data.local.dao.TagDao
import com.taskmanager.data.local.dao.TaskTagDao
import com.taskmanager.data.local.entity.TaskTagCrossRef
import com.taskmanager.domain.model.Tag
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.repository.TagRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TagRepositoryImpl @Inject constructor(
    private val tagDao: TagDao,
    private val taskTagDao: TaskTagDao
) : TagRepository {

    override suspend fun createTag(tag: Tag): Long = tagDao.insert(tag.toEntity())

    override suspend fun deleteTag(id: Long) {
        tagDao.deleteById(id)
    }

    override fun getAllTags(): Flow<List<Tag>> =
        tagDao.getAll().map { list -> list.map { it.toDomain() } }

    override fun getTagsForTask(taskId: Long): Flow<List<Tag>> =
        taskTagDao.getTagsForTask(taskId).map { list -> list.map { it.toDomain() } }

    override suspend fun setTagsForTask(taskId: Long, tagNames: List<String>): List<Long> {
        taskTagDao.deleteAllForTask(taskId)
        return tagNames.distinct().map { name -> ensureTagAndAttach(taskId, name) }
    }

    override suspend fun addTagToTask(taskId: Long, tagName: String): Long =
        ensureTagAndAttach(taskId, tagName)

    override suspend fun removeTagFromTask(taskId: Long, tagName: String) {
        val tag = tagDao.findByName(tagName) ?: return
        taskTagDao.delete(taskId, tag.id)
    }

    override fun getTasksByTag(tagName: String): Flow<List<Task>> =
        taskTagDao.getTasksByTag(tagName).map { list -> list.map { it.toDomain() } }

    /**
     * Insert-or-find the tag by name (case-insensitive), then attach it to the
     * task (IGNORE so duplicates are harmless). Returns the tag id.
     */
    private suspend fun ensureTagAndAttach(taskId: Long, tagName: String): Long {
        val existing = tagDao.findByName(tagName)
        val tagId = if (existing != null) {
            existing.id
        } else {
            val inserted = tagDao.insertOrIgnore(Tag(name = tagName).toEntity())
            if (inserted == -1L) {
                // Race: another insert happened; fetch the id.
                tagDao.findByName(tagName)?.id ?: error("Failed to resolve tag '$tagName'")
            } else {
                inserted
            }
        }
        taskTagDao.insert(TaskTagCrossRef(taskId = taskId, tagId = tagId))
        return tagId
    }
}
