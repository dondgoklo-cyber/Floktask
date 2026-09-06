package com.taskmanager.domain.usecase.subtask

import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.model.Subtask
import com.taskmanager.domain.repository.SubtaskRepository
import javax.inject.Inject

/**
 * Use case for retrieving subtask tree for a task.
 * Part of Clean Architecture - Presentation layer depends only on UseCases, not Repositories.
 */
class GetSubtaskTreeUseCase @Inject constructor(
    private val subtaskRepository: SubtaskRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(taskId: Long): List<Subtask> = runCatching {
        subtaskRepository.getSubtaskTree(taskId)
    }.onFailure { e ->
        logger.error("GetSubtaskTreeUseCase", "Error getting subtask tree", e)
    }.getOrDefault(emptyList())
}
