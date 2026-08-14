package com.taskmanager.domain.usecase.project

import com.taskmanager.domain.model.Project
import com.taskmanager.domain.repository.ProjectRepository
import javax.inject.Inject

class CreateProjectUseCase @Inject constructor(
    private val projectRepository: ProjectRepository
) {
    suspend operator fun invoke(project: Project): Long =
        projectRepository.createProject(project)
}
