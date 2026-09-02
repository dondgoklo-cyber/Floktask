package com.taskmanager.domain.usecase.project

import com.taskmanager.domain.repository.ProjectRepository
import javax.inject.Inject

/**
 * Use case to update an existing project.
 */
class UpdateProjectUseCase @Inject constructor(
    private val projectRepository: ProjectRepository
) {
    suspend operator fun invoke(project: com.taskmanager.domain.model.Project) {
        projectRepository.updateProject(project)
    }
}
