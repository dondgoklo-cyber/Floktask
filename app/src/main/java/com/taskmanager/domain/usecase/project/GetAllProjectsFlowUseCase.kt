package com.taskmanager.domain.usecase.project

import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.model.Project
import com.taskmanager.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving all projects as Flow.
 * Part of Clean Architecture - Presentation layer depends only on UseCases, not Repositories.
 */
class GetAllProjectsFlowUseCase @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val logger: Logger
) {
    operator fun invoke(): Flow<List<Project>> = projectRepository.getAllProjects()
}
