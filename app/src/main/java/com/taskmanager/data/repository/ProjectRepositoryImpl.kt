package com.taskmanager.data.repository
import com.taskmanager.domain.logger.Logger

import com.taskmanager.data.local.dao.ProjectDao
import com.taskmanager.domain.model.Project
import com.taskmanager.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject

class ProjectRepositoryImpl @Inject constructor(
    private val logger: Logger,
    private val projectDao: ProjectDao
) : ProjectRepository {

    override suspend fun createProject(project: Project): Long = try {
        projectDao.insert(project.toEntity())
    } catch (e: Exception) {
        logger.error("ProjectRepositoryImpl", "Error in Long", e)
        throw e
    }

    override suspend fun getProjectById(id: Long): Project? = try {
        projectDao.getById(id)?.toDomain()
    } catch (e: Exception) {
        logger.error("ProjectRepositoryImpl", "Error in Project?", e)
        throw e
    }

    override suspend fun updateProject(project: Project) {
        try {
            projectDao.update(project.copy(updatedAt = Instant.now()).toEntity())
        } catch (e: Exception) {
            logger.error("ProjectRepositoryImpl", "Error in Project", e)
            throw e
        }
    }

    override suspend fun deleteProject(id: Long) {
        try {
            projectDao.deleteById(id)
        } catch (e: Exception) {
            logger.error("ProjectRepositoryImpl", "Error in Long", e)
            throw e
        }
    }

    override suspend fun archiveProject(id: Long, archived: Boolean) {
        try {
            projectDao.setArchived(id, archived, Instant.now().toEpochMilli())
        } catch (e: Exception) {
            logger.error("ProjectRepositoryImpl", "Error in Boolean", e)
            throw e
        }
    }

    override fun getAllProjects(): Flow<List<Project>> = try {
        projectDao.getAll().map { list -> list.map { it.toDomain() } }
    } catch (e: Exception) {
        logger.error("ProjectRepositoryImpl", "Error in Flow<List<Project>>", e)
        throw e
    }

    override fun getActiveProjects(): Flow<List<Project>> =
        projectDao.getActive().map { list -> list.map { it.toDomain() } }

    override fun getArchivedProjects(): Flow<List<Project>> =
        projectDao.getArchived().map { list -> list.map { it.toDomain() } }
}
