package com.taskmanager.domain.usecase.subtask

import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.model.Subtask
import com.taskmanager.domain.repository.SubtaskRepository
import javax.inject.Inject

/**
 * Use case for updating a subtask.
 * Part of Clean Architecture - Presentation layer depends only on UseCases, not Repositories.
 */
class UpdateSubtaskUseCase @Inject constructor(
    private val subtaskRepository: SubtaskRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(subtask: Subtask) = runCatching {
        subtaskRepository.updateSubtask(subtask)
    }.onFailure { e ->
        logger.error("UpdateSubtaskUseCase", "Error updating subtask", e)
    }
}
