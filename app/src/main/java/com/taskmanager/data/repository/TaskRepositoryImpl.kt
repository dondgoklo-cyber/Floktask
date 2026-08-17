package com.taskmanager.data.repository

import com.taskmanager.data.local.dao.TaskDao
import com.taskmanager.data.local.dao.TaskTagDao
import com.taskmanager.data.local.dao.TagDao
import com.taskmanager.data.local.entity.TaskTagEntity
import com.taskmanager.domain.model.Tag
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val taskTagDao: TaskTagDao,
    private val tagDao: TagDao
) : TaskRepository {

    override suspend fun createTask(task: Task): Long {
        val taskId = taskDao.insert(task.toEntity())
        // Если у задачи есть теги, сохраняем их
        if (task.tags.isNotEmpty()) {
            val tagIds = task.tags.mapNotNull { tagName ->
                tagDao.getByName(tagName)?.id
            }
            if (tagIds.isNotEmpty()) {
                setTaskTags(taskId, tagIds)
            }
        }
        return taskId
    }

    override suspend fun getTaskById(id: Long): Task? {
        val taskEntity = taskDao.getById(id) ?: return null
        val task = taskEntity.toDomain()
        // Загружаем теги для задачи
        val tagIds = taskTagDao.getTagIdsForTask(id)
        val tags = tagIds.mapNotNull { tagId -> tagDao.getById(tagId)?.toDomain() }
        return task.copy(tags = tags.map { it.name })
    }

    override suspend fun updateTask(task: Task) {
        taskDao.update(task.copy(updatedAt = Instant.now()).toEntity())
        // Обновляем теги
        if (task.id != null) {
            val tagIds = task.tags.mapNotNull { tagName ->
                tagDao.getByName(tagName)?.id
            }
            setTaskTags(task.id, tagIds)
        }
    }

    override suspend fun deleteTask(id: Long) {
        taskDao.deleteById(id)
    }

    override suspend fun setCompleted(id: Long, completed: Boolean) {
        taskDao.setCompleted(id, completed, Instant.now().toEpochMilli())
    }

    override fun getAllTasks(): Flow<List<Task>> =
        combine(
            taskDao.getAll(),
            taskTagDao.getAll(),
            tagDao.getAll()
        ) { tasks, taskTags, tagEntities ->
            val tagMap = tagEntities.associateBy { it.id }
            tasks.map { taskEntity ->
                val tagIds = taskTags.filter { it.taskId == taskEntity.id }.map { it.tagId }
                val tags = tagIds.mapNotNull { tagMap[it]?.toDomain() }
                taskEntity.toDomain().copy(tags = tags.map { it.name })
            }
        }

    override fun getTasksByProject(projectId: Long): Flow<List<Task>> =
        combine(
            taskDao.getByProject(projectId),
            taskTagDao.getAll(),
            tagDao.getAll()
        ) { tasks, taskTags, tagEntities ->
            val tagMap = tagEntities.associateBy { it.id }
            tasks.map { taskEntity ->
                val tagIds = taskTags.filter { it.taskId == taskEntity.id }.map { it.tagId }
                val tags = tagIds.mapNotNull { tagMap[it]?.toDomain() }
                taskEntity.toDomain().copy(tags = tags.map { it.name })
            }
        }

    override fun getCompletedTasks(): Flow<List<Task>> =
        combine(
            taskDao.getCompletedTasks(),
            taskTagDao.getAll(),
            tagDao.getAll()
        ) { tasks, taskTags, tagEntities ->
            val tagMap = tagEntities.associateBy { it.id }
            tasks.map { taskEntity ->
                val tagIds = taskTags.filter { it.taskId == taskEntity.id }.map { it.tagId }
                val tags = tagIds.mapNotNull { tagMap[it]?.toDomain() }
                taskEntity.toDomain().copy(tags = tags.map { it.name })
            }
        }

    override fun getIncompleteTasks(): Flow<List<Task>> =
        combine(
            taskDao.getIncompleteTasks(),
            taskTagDao.getAll(),
            tagDao.getAll()
        ) { tasks, taskTags, tagEntities ->
            val tagMap = tagEntities.associateBy { it.id }
            tasks.map { taskEntity ->
                val tagIds = taskTags.filter { it.taskId == taskEntity.id }.map { it.tagId }
                val tags = tagIds.mapNotNull { tagMap[it]?.toDomain() }
                taskEntity.toDomain().copy(tags = tags.map { it.name })
            }
        }

    override fun searchTasks(query: String): Flow<List<Task>> =
        combine(
            taskDao.search("%$query%"),
            taskTagDao.getAll(),
            tagDao.getAll()
        ) { tasks, taskTags, tagEntities ->
            val tagMap = tagEntities.associateBy { it.id }
            tasks.map { taskEntity ->
                val tagIds = taskTags.filter { it.taskId == taskEntity.id }.map { it.tagId }
                val tags = tagIds.mapNotNull { tagMap[it]?.toDomain() }
                taskEntity.toDomain().copy(tags = tags.map { it.name })
            }
        }

    override fun getTimedTasksForDay(dayStart: Long, dayEnd: Long): Flow<List<Task>> =
        combine(
            taskDao.getTimedTasksForDay(dayStart, dayEnd),
            taskTagDao.getAll(),
            tagDao.getAll()
        ) { tasks, taskTags, tagEntities ->
            val tagMap = tagEntities.associateBy { it.id }
            tasks.map { taskEntity ->
                val tagIds = taskTags.filter { it.taskId == taskEntity.id }.map { it.tagId }
                val tags = tagIds.mapNotNull { tagMap[it]?.toDomain() }
                taskEntity.toDomain().copy(tags = tags.map { it.name })
            }
        }

    override fun getTasksForDay(dayStart: Long, dayEnd: Long): Flow<List<Task>> =
        combine(
            taskDao.getTasksForDay(dayStart, dayEnd),
            taskTagDao.getAll(),
            tagDao.getAll()
        ) { tasks, taskTags, tagEntities ->
            val tagMap = tagEntities.associateBy { it.id }
            tasks.map { taskEntity ->
                val tagIds = taskTags.filter { it.taskId == taskEntity.id }.map { it.tagId }
                val tags = tagIds.mapNotNull { tagMap[it]?.toDomain() }
                taskEntity.toDomain().copy(tags = tags.map { it.name })
            }
        }

    override fun getTasksByEisenhowerQuadrant(quadrantName: String): Flow<List<Task>> =
        combine(
            taskDao.getTasksByQuadrant(quadrantName),
            taskTagDao.getAll(),
            tagDao.getAll()
        ) { tasks, taskTags, tagEntities ->
            val tagMap = tagEntities.associateBy { it.id }
            tasks.map { taskEntity ->
                val tagIds = taskTags.filter { it.taskId == taskEntity.id }.map { it.tagId }
                val tags = tagIds.mapNotNull { tagMap[it]?.toDomain() }
                taskEntity.toDomain().copy(tags = tags.map { it.name })
            }
        }

    override fun getInboxTasks(): Flow<List<Task>> =
        combine(
            taskDao.getInboxTasks(),
            taskTagDao.getAll(),
            tagDao.getAll()
        ) { tasks, taskTags, tagEntities ->
            val tagMap = tagEntities.associateBy { it.id }
            tasks.map { taskEntity ->
                val tagIds = taskTags.filter { it.taskId == taskEntity.id }.map { it.tagId }
                val tags = tagIds.mapNotNull { tagMap[it]?.toDomain() }
                taskEntity.toDomain().copy(tags = tags.map { it.name })
            }
        }

    override fun getUpcomingTasks(fromEpoch: Long): Flow<List<Task>> =
        combine(
            taskDao.getUpcomingTasks(fromEpoch),
            taskTagDao.getAll(),
            tagDao.getAll()
        ) { tasks, taskTags, tagEntities ->
            val tagMap = tagEntities.associateBy { it.id }
            tasks.map { taskEntity ->
                val tagIds = taskTags.filter { it.taskId == taskEntity.id }.map { it.tagId }
                val tags = tagIds.mapNotNull { tagMap[it]?.toDomain() }
                taskEntity.toDomain().copy(tags = tags.map { it.name })
            }
        }

    override suspend fun setTaskTags(taskId: Long, tagIds: List<Long>) {
        taskTagDao.deleteByTaskId(taskId)
        taskTagDao.insertAll(tagIds.map { TaskTagEntity(taskId = taskId, tagId = it) })
    }

    override suspend fun getTaskTags(taskId: Long): List<Tag> {
        val tagIds = taskTagDao.getTagIdsForTask(taskId)
        return tagIds.mapNotNull { id -> tagDao.getById(id)?.toDomain() }
    }

    override fun getTasksByTag(tagId: Long): Flow<List<Task>> =
        taskTagDao.getTasksForTag(tagId).map { list ->
            list.map { it.toDomain() }
        }
}
