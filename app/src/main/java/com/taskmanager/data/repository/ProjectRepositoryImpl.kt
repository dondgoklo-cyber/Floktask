package com.taskmanager.data.repository

import com.taskmanager.data.local.dao.ProjectDao
import com.taskmanager.domain.model.Project
import com.taskmanager.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject

class ProjectRepositoryImpl @Inject constructor(
    private val projectDao: ProjectDao
) : ProjectRepository {

    override suspend fun createProject(project: Project): Long =
        projectDao.insert(project.toEntity())

    override suspend fun getProjectById(id: Long): Project? =
        projectDao.getById(id)?.toDomain()

    override suspend fun findProjectByName(name: String): Project? =
        projectDao.findByName(name)?.toDomain()

    override suspend fun updateProject(project: Project) {
        projectDao.update(project.copy(updatedAt = Instant.now()).toEntity())
    }

    override suspend fun deleteProject(id: Long) {
        projectDao.deleteById(id)
    }

    override suspend fun archiveProject(id: Long, archived: Boolean) {
        projectDao.setArchived(id, archived, Instant.now().toEpochMilli())
    }

    override fun getAllProjects(): Flow<List<Project>> =
        projectDao.getAll().map { list -> list.map { it.toDomain() } }

    override fun getActiveProjects(): Flow<List<Project>> =
        projectDao.getActive().map { list -> list.map { it.toDomain() } }

    override fun getArchivedProjects(): Flow<List<Project>> =
        projectDao.getArchived().map { list -> list.map { it.toDomain() } }
}
