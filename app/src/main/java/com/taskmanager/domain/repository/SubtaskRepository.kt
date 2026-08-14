package com.taskmanager.domain.repository

import com.taskmanager.domain.model.Subtask
import kotlinx.coroutines.flow.Flow

interface SubtaskRepository {
    suspend fun createSubtask(subtask: Subtask): Long
    suspend fun updateSubtask(subtask: Subtask)
    suspend fun deleteSubtask(id: Long)
    suspend fun setCompleted(id: Long, completed: Boolean)

    fun observeByTask(taskId: Long): Flow<List<Subtask>>
    suspend fun getByTask(taskId: Long): List<Subtask>

    /** Переупорядочивает подзадачи задачи: перенести подзадачу [fromIndex] в [toIndex]. */
    suspend fun reorderSubtasks(taskId: Long, fromIndex: Int, toIndex: Int)
}
