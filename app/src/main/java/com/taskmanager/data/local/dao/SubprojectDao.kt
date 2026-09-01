package com.taskmanager.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.taskmanager.data.local.entity.SubprojectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubprojectDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(subproject: SubprojectEntity): Long

    @Update
    suspend fun update(subproject: SubprojectEntity)

    @Delete
    suspend fun delete(subproject: SubprojectEntity)

    @Query("SELECT * FROM subprojects WHERE id = :id")
    suspend fun getById(id: Long): SubprojectEntity?

    @Query("SELECT * FROM subprojects")
    fun getAll(): Flow<List<SubprojectEntity>>

    @Query("SELECT * FROM subprojects WHERE parentProjectId = :projectId AND parentSubprojectId IS NULL")
    fun getByProject(projectId: Long): Flow<List<SubprojectEntity>>

    @Query("SELECT * FROM subprojects WHERE parentSubprojectId = :subprojectId")
    fun getByParentSubproject(subprojectId: Long): Flow<List<SubprojectEntity>>

    @Query("SELECT * FROM subprojects WHERE parentProjectId = :projectId")
    fun getAllByProject(projectId: Long): Flow<List<SubprojectEntity>>

    @Query("SELECT * FROM subprojects WHERE isArchived = 0")
    fun getActive(): Flow<List<SubprojectEntity>>

    @Query("SELECT * FROM subprojects WHERE isArchived = 1")
    fun getArchived(): Flow<List<SubprojectEntity>>

    @Query("SELECT * FROM subprojects WHERE parentProjectId = :projectId AND parentSubprojectId IS NULL AND isArchived = 0 ORDER BY orderIndex")
    fun getActiveByProject(projectId: Long): Flow<List<SubprojectEntity>>

    @Query("DELETE FROM subprojects WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE subprojects SET isArchived = :archived, updatedAt = :updatedAt WHERE id = :id")
    suspend fun setArchived(id: Long, archived: Boolean, updatedAt: Long)

    @Query("UPDATE subprojects SET orderIndex = :newIndex, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateOrder(id: Long, newIndex: Int, updatedAt: Long)

    @Query("SELECT COUNT(*) FROM subprojects WHERE parentProjectId = :projectId")
    suspend fun countByProject(projectId: Long): Int

    @Query("SELECT COUNT(*) FROM subprojects WHERE parentSubprojectId = :subprojectId")
    suspend fun countByParentSubproject(subprojectId: Long): Int
}
