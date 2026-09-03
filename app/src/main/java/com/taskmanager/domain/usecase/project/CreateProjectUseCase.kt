package com.taskmanager.domain.usecase.project

import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.model.Project
import com.taskmanager.domain.repository.ProjectRepository
import javax.inject.Inject

class CreateProjectUseCase @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(project: Project): Long = runCatching {
        projectRepository.createProject(project)
    }.onFailure { e ->
        logger.error("CreateProjectUseCase", "Error in invoke", e)
    }.getOrThrow()
}
