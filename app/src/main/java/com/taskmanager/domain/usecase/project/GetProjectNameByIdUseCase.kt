package com.taskmanager.domain.usecase.project

import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.repository.ProjectRepository
import javax.inject.Inject

/**
 * Use case for retrieving project name by ID.
 * Part of Clean Architecture - Presentation layer depends only on UseCases, not Repositories.
 */
class GetProjectNameByIdUseCase @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(projectId: Long): String? = runCatching {
        projectRepository.getProjectById(projectId)?.title
    }.onFailure { e ->
        logger.error("GetProjectNameByIdUseCase", "Error getting project name", e)
    }.getOrNull()
}
