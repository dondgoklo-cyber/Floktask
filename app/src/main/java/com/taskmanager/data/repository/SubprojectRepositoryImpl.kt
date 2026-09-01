package com.taskmanager.data.repository

import com.taskmanager.data.local.dao.SubprojectDao
import com.taskmanager.domain.model.Subproject
import com.taskmanager.domain.repository.SubprojectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject

class SubprojectRepositoryImpl @Inject constructor(
    private val subprojectDao: SubprojectDao
) : SubprojectRepository {

    override suspend fun createSubproject(subproject: Subproject): Long =
        subprojectDao.insert(subproject.toEntity())

    override suspend fun getSubprojectById(id: Long): Subproject? =
        subprojectDao.getById(id)?.toDomain()

    override suspend fun updateSubproject(subproject: Subproject) {
        subprojectDao.update(subproject.copy(updatedAt = Instant.now()).toEntity())
    }

    override suspend fun deleteSubproject(id: Long) {
        subprojectDao.deleteById(id)
    }

    override suspend fun setArchived(id: Long, archived: Boolean) {
        subprojectDao.setArchived(id, archived, Instant.now().toEpochMilli())
    }

    override suspend fun updateOrder(id: Long, newIndex: Int) {
        subprojectDao.updateOrder(id, newIndex, Instant.now().toEpochMilli())
    }

    override fun getAllSubprojects(): Flow<List<Subproject>> =
        subprojectDao.getAll().map { list -> list.map { it.toDomain() } }

    override fun getSubprojectsByProject(projectId: Long): Flow<List<Subproject>> =
        subprojectDao.getByProject(projectId).map { list -> list.map { it.toDomain() } }

    override fun getSubprojectsByParentSubproject(parentId: Long): Flow<List<Subproject>> =
        subprojectDao.getByParentSubproject(parentId).map { list -> list.map { it.toDomain() } }

    override fun getActiveSubprojectsByProject(projectId: Long): Flow<List<Subproject>> =
        subprojectDao.getActiveByProject(projectId).map { list -> list.map { it.toDomain() } }

    override fun getAllByProject(projectId: Long): Flow<List<Subproject>> =
        subprojectDao.getAllByProject(projectId).map { list -> list.map { it.toDomain() } }

    override suspend fun getSubprojectCountByProject(projectId: Long): Int =
        subprojectDao.countByProject(projectId)

    override suspend fun getSubprojectCountByParent(parentId: Long): Int =
        subprojectDao.countByParentSubproject(parentId)

    override suspend fun getProjectHierarchy(projectId: Long): List<Subproject> {
        val topLevel = subprojectDao.getByProject(projectId)
        val result = mutableListOf<Subproject>()
        
        // Add top-level subprojects
        topLevel.collect { entities ->
            result.addAll(entities.map { it.toDomain() })
            
            // Add nested subprojects for each top-level
            entities.forEach { topEntity ->
                val nested = subprojectDao.getByParentSubproject(topEntity.id)
                nested.collect { nestedEntities ->
                    result.addAll(nestedEntities.map { it.toDomain() })
                }
            }
        }
        
        return result
    }

    override suspend fun getSubprojectWithChildren(id: Long): Pair<Subproject, List<Subproject>>? {
        val subproject = getSubprojectById(id) ?: return null
        val children = subprojectDao.getByParentSubproject(id).map { list -> list.map { it.toDomain() } }
        
        var childrenList = emptyList<Subproject>()
        children.collect { childrenList = it }
        
        return Pair(subproject, childrenList)
    }
}
