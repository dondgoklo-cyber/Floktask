package com.taskmanager.domain.usecase.subtask

import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.repository.SubtaskRepository
import javax.inject.Inject

/**
 * Use case for deleting a subtask.
 * Part of Clean Architecture - Presentation layer depends only on UseCases, not Repositories.
 */
class DeleteSubtaskUseCase @Inject constructor(
    private val subtaskRepository: SubtaskRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(subtaskId: Long) = runCatching {
        subtaskRepository.deleteSubtask(subtaskId)
    }.onFailure { e ->
        logger.error("DeleteSubtaskUseCase", "Error deleting subtask", e)
    }
}
