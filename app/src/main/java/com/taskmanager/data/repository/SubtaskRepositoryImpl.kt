package com.taskmanager.data.repository

import com.taskmanager.data.local.dao.SubtaskDao
import com.taskmanager.domain.model.Subtask
import com.taskmanager.domain.repository.SubtaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import android.util.Log

class SubtaskRepositoryImpl @Inject constructor(
    private val subtaskDao: SubtaskDao
) : SubtaskRepository {

    override suspend fun createSubtask(subtask: Subtask): Long = try {
        subtaskDao.insert(subtask.toEntity())
    } catch (e: Exception) {
        Log.e("SubtaskRepositoryImpl", "Error in Long", e)
        throw e
    }

    override suspend fun updateSubtask(subtask: Subtask) {
        try {
            subtaskDao.update(subtask.toEntity())
        } catch (e: Exception) {
            Log.e("SubtaskRepositoryImpl", "Error in Subtask", e)
            throw e
        }
    }

    override suspend fun deleteSubtask(id: Long) {
        try {
            subtaskDao.deleteById(id)
        } catch (e: Exception) {
            Log.e("SubtaskRepositoryImpl", "Error in Long", e)
            throw e
        }
    }

    override suspend fun setCompleted(id: Long, completed: Boolean) {
        try {
            subtaskDao.setCompleted(id, completed)
        } catch (e: Exception) {
            Log.e("SubtaskRepositoryImpl", "Error in Boolean", e)
            throw e
        }
    }

    override fun observeByTask(taskId: Long): Flow<List<Subtask>> = try {
        subtaskDao.getByTask(taskId).map { list -> list.map { it.toDomain() } }
    } catch (e: Exception) {
        Log.e("SubtaskRepositoryImpl", "Error in Flow<List<Subtask>>", e)
        throw e
    }

    override suspend fun getByTask(taskId: Long): List<Subtask> = try {
        subtaskDao.getListByTask(taskId).map { it.toDomain() }
    } catch (e: Exception) {
        Log.e("SubtaskRepositoryImpl", "Error in List<Subtask>", e)
        throw e
    }

    override suspend fun getSubtaskTree(taskId: Long): List<Subtask> {
        val all = try {
            subtaskDao.getListByTask(taskId).map { it.toDomain() }
        } catch (e: Exception) {
            Log.e("SubtaskRepositoryImpl", "Error in all", e)
            throw e
        }
        return all
    }

    override suspend fun reorderSubtasks(taskId: Long, fromIndex: Int, toIndex: Int) {
        try {
            val subtasks = subtaskDao.getListByTask(taskId).map { it.toDomain() }.toMutableList()
            if (fromIndex < 0 || fromIndex >= subtasks.size) return
            if (toIndex < 0 || toIndex > subtasks.size) return
            
            val item = subtasks.removeAt(fromIndex)
            subtasks.add(toIndex.coerceAtMost(subtasks.size), item)
            
            subtasks.forEachIndexed { index, subtask ->
                subtaskDao.updateOrder(subtask.id, index)
            }
        } catch (e: Exception) {
            Log.e("SubtaskRepositoryImpl", "Error in reorderSubtasks", e)
            throw e
        }
    }
}
