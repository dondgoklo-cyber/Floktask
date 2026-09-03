package com.taskmanager.domain.usecase.project

import com.taskmanager.domain.model.Project
import com.taskmanager.domain.repository.ProjectRepository
import javax.inject.Inject
import android.util.Log

class CreateProjectUseCase @Inject constructor(
    private val projectRepository: ProjectRepository
) {
    suspend operator fun invoke(project: Project): Long = runCatching {
        projectRepository.createProject(project)
    }.onFailure { e ->
        Log.e("CreateProjectUseCase", "Error in invoke", e)
    }.getOrThrow()
}
