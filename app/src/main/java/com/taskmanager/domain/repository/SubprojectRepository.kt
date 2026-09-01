package com.taskmanager.domain.repository

import com.taskmanager.domain.model.Subproject
import kotlinx.coroutines.flow.Flow

interface SubprojectRepository {
    suspend fun createSubproject(subproject: Subproject): Long
    suspend fun getSubprojectById(id: Long): Subproject?
    suspend fun updateSubproject(subproject: Subproject)
    suspend fun deleteSubproject(id: Long)
    suspend fun setArchived(id: Long, archived: Boolean)
    suspend fun updateOrder(id: Long, newIndex: Int)

    fun getAllSubprojects(): Flow<List<Subproject>>
    fun getSubprojectsByProject(projectId: Long): Flow<List<Subproject>>
    fun getSubprojectsByParentSubproject(parentId: Long): Flow<List<Subproject>>
    fun getActiveSubprojectsByProject(projectId: Long): Flow<List<Subproject>>
    fun getAllByProject(projectId: Long): Flow<List<Subproject>>

    suspend fun getSubprojectCountByProject(projectId: Long): Int
    suspend fun getSubprojectCountByParent(parentId: Long): Int

    /**
     * Get the full hierarchy for a project including nested subprojects
     */
    suspend fun getProjectHierarchy(projectId: Long): List<Subproject>

    /**
     * Get subproject with its children (nested subprojects)
     */
    suspend fun getSubprojectWithChildren(id: Long): Pair<Subproject, List<Subproject>>?
}
