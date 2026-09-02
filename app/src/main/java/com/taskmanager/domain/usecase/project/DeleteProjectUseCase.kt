package com.taskmanager.domain.usecase.project

import com.taskmanager.domain.repository.ProjectRepository
import javax.inject.Inject

/**
 * Use case to delete a project.
 */
class DeleteProjectUseCase @Inject constructor(
    private val projectRepository: ProjectRepository
) {
    suspend operator fun invoke(projectId: Long) {
        projectRepository.deleteProject(projectId)
    }
}
