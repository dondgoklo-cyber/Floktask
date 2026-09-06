package com.taskmanager.domain.usecase.subtask

import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.repository.SubtaskRepository
import javax.inject.Inject

/**
 * Use case for setting subtask completion status.
 * Part of Clean Architecture - Presentation layer depends only on UseCases, not Repositories.
 */
class SetSubtaskCompletedUseCase @Inject constructor(
    private val subtaskRepository: SubtaskRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(subtaskId: Long, completed: Boolean) = runCatching {
        subtaskRepository.setCompleted(subtaskId, completed)
    }.onFailure { e ->
        logger.error("SetSubtaskCompletedUseCase", "Error setting subtask completed", e)
    }
}
