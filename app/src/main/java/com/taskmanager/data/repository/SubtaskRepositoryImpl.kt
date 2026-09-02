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
        
    } catch (e: Exception) {
        Log.e("SubtaskRepositoryImpl", "Error in Long", e)
        throw e
    }
        subtaskDao.insert(subtask.toEntity())

    override suspend fun updateSubtask(subtask: Subtask) {
        try {
            subtaskDao.update(subtask.toEntity())
        } catch (e: Exception) {
            Log.e("SubtaskRepositoryImpl", "Error in Subtask)", e)
            throw e
        }
    }

    override suspend fun deleteSubtask(id: Long) {
        try {
            subtaskDao.deleteById(id)
        } catch (e: Exception) {
            Log.e("SubtaskRepositoryImpl", "Error in Long)", e)
            throw e
        }
    }

    override suspend fun setCompleted(id: Long, completed: Boolean) {
        try {
            subtaskDao.setCompleted(id, completed)
        } catch (e: Exception) {
            Log.e("SubtaskRepositoryImpl", "Error in Boolean)", e)
            throw e
        }
    }

    override fun observeByTask(taskId: Long): Flow<List<Subtask>> = try {
        
    } catch (e: Exception) {
        Log.e("SubtaskRepositoryImpl", "Error in Flow<List<Subtask>>", e)
        throw e
    }
        subtaskDao.getByTask(taskId).map { list -> list.map { it.toDomain() } }

    override suspend fun getByTask(taskId: Long): List<Subtask> = try {
        
    } catch (e: Exception) {
        Log.e("SubtaskRepositoryImpl", "Error in List<Subtask>", e)
        throw e
    }
        subtaskDao.getListByTask(taskId).map { it.toDomain() }

    override suspend fun getSubtaskTree(taskId: Long): List<Subtask> {
        val all = try {
        subtaskDao.getListByTask(taskId).map { it.toDomain() }
    } catch (e: Exception) {
        Log.e("SubtaskRepositoryImpl", "Error in all", e)
        throw e
    }
        return buildTree(all, maxDepth = 5)
    }

    private fun buildTree(all: List<Subtask>, maxDepth: Int): List<Subtask> {
        val byParent = all.groupBy { it.parentSubtaskId }
        fun childrenOf(parentId: Long?, depth: Int): List<Subtask> {
            if (depth > maxDepth) return emptyList()
            return (byParent[parentId] ?: emptyList()).map { subtask ->
                subtask.copy(children = childrenOf(subtask.id, depth + 1))
            }
        }
        return childrenOf(null, 1)
    }

    override suspend fun reorderSubtasks(taskId: Long, fromIndex: Int, toIndex: Int) {
        try {
            val list = subtaskDao.getListByTask(taskId).toMutableList()
        if (fromIndex !in list.indices || toIndex !in list.indices) return
        val moved = list.removeAt(fromIndex)
        list.add(toIndex, moved)
        list.forEachIndexed { index, entity ->
            if (entity.orderIndex != index) {
                subtaskDao.update(entity.copy(orderIndex = index))
        } catch (e: Exception) {
            Log.e("SubtaskRepositoryImpl", "Error in Int)", e)
            throw e
        }
    }
        }
    }
}
