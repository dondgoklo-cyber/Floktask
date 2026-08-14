package com.taskmanager.data.repository

import com.taskmanager.data.local.dao.SubtaskDao
import com.taskmanager.domain.model.Subtask
import com.taskmanager.domain.repository.SubtaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SubtaskRepositoryImpl @Inject constructor(
    private val subtaskDao: SubtaskDao
) : SubtaskRepository {

    override suspend fun createSubtask(subtask: Subtask): Long =
        subtaskDao.insert(subtask.toEntity())

    override suspend fun updateSubtask(subtask: Subtask) {
        subtaskDao.update(subtask.toEntity())
    }

    override suspend fun deleteSubtask(id: Long) {
        subtaskDao.deleteById(id)
    }

    override suspend fun setCompleted(id: Long, completed: Boolean) {
        subtaskDao.setCompleted(id, completed)
    }

    override fun observeByTask(taskId: Long): Flow<List<Subtask>> =
        subtaskDao.getByTask(taskId).map { list -> list.map { it.toDomain() } }

    override suspend fun getByTask(taskId: Long): List<Subtask> =
        subtaskDao.getListByTask(taskId).map { it.toDomain() }
}
