package com.taskmanager.domain.usecase.project

import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.model.Project
import com.taskmanager.domain.repository.ProjectRepository
import javax.inject.Inject

/**
 * Use case for retrieving a project by ID.
 * Part of Clean Architecture - Presentation layer depends only on UseCases, not Repositories.
 */
class GetProjectByIdUseCase @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(id: Long): Project? = runCatching {
        projectRepository.getProjectById(id)
    }.onFailure { e ->
        logger.error("GetProjectByIdUseCase", "Error getting project by id", e)
    }.getOrNull()
}
