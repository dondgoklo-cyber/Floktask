package com.taskmanager.domain.usecase.subtask

import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.model.Subtask
import com.taskmanager.domain.repository.SubtaskRepository
import javax.inject.Inject

/**
 * Use case for creating a subtask.
 * Part of Clean Architecture - Presentation layer depends only on UseCases, not Repositories.
 */
class CreateSubtaskUseCase @Inject constructor(
    private val subtaskRepository: SubtaskRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(subtask: Subtask): Long = runCatching {
        subtaskRepository.createSubtask(subtask)
    }.onFailure { e ->
        logger.error("CreateSubtaskUseCase", "Error creating subtask", e)
    }.getOrThrow()
}
