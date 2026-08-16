package com.taskmanager.domain.repository

import com.taskmanager.domain.model.Project
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {
    suspend fun createProject(project: Project): Long
    suspend fun getProjectById(id: Long): Project?
    suspend fun findProjectByName(name: String): Project?
    suspend fun updateProject(project: Project)
    suspend fun deleteProject(id: Long)
    suspend fun archiveProject(id: Long, archived: Boolean)

    fun getAllProjects(): Flow<List<Project>>
    fun getActiveProjects(): Flow<List<Project>>
    fun getArchivedProjects(): Flow<List<Project>>
}
