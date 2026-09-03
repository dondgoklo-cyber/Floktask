package com.taskmanager.domain.usecase.project

import com.taskmanager.domain.model.Project
import com.taskmanager.domain.repository.ProjectRepository
import javax.inject.Inject
import com.taskmanager.domain.logger.Logger

class CreateProjectUseCase @Inject constructor( 
    private val logger: Logger,
    private val projectRepository: ProjectRepository
) {
    suspend operator fun invoke(project: Project): Long = runCatching {
        projectRepository.createProject(project)
    }.onFailure { e ->
        logger.error("CreateProjectUseCase", "Error in invoke", e)
    }.getOrThrow()
}
