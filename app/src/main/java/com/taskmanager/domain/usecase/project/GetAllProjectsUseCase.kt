package com.taskmanager.domain.usecase.project

import com.taskmanager.domain.model.Project
import com.taskmanager.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllProjectsUseCase @Inject constructor(
    private val projectRepository: ProjectRepository
) {
    operator fun invoke(): Flow<List<Project>> = projectRepository.getActiveProjects()
}
