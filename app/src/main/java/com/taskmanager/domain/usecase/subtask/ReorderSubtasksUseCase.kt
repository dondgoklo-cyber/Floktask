package com.taskmanager.domain.usecase.subtask

import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.repository.SubtaskRepository
import javax.inject.Inject

/**
 * Use case for reordering subtasks.
 * Part of Clean Architecture - Presentation layer depends only on UseCases, not Repositories.
 */
class ReorderSubtasksUseCase @Inject constructor(
    private val subtaskRepository: SubtaskRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(taskId: Long, fromIndex: Int, toIndex: Int) = runCatching {
        subtaskRepository.reorderSubtasks(taskId, fromIndex, toIndex)
    }.onFailure { e ->
        logger.error("ReorderSubtasksUseCase", "Error reordering subtasks", e)
    }
}
