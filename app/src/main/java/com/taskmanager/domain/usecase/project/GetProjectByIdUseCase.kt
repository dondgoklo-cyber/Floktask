package com.taskmanager.domain.usecase.project

import com.taskmanager.domain.repository.ProjectRepository
import javax.inject.Inject

/**
 * Use case to retrieve a single project by its ID.
 */
class GetProjectByIdUseCase @Inject constructor(
    private val projectRepository: ProjectRepository
) {
    suspend operator fun invoke(projectId: Long): com.taskmanager.domain.model.Project? {
        return projectRepository.getProjectById(projectId)
    }
}
