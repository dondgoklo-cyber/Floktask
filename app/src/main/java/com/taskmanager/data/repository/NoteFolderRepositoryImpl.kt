package com.taskmanager.data.repository

import com.taskmanager.data.local.dao.NoteFolderDao
import com.taskmanager.domain.model.NoteFolder
import com.taskmanager.domain.repository.NoteFolderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import android.util.Log

class NoteFolderRepositoryImpl @Inject constructor(
    private val folderDao: NoteFolderDao
) : NoteFolderRepository {

    override suspend fun createFolder(folder: NoteFolder): Long = try {
        folderDao.insert(folder.toEntity())
    } catch (e: Exception) {
        Log.e("NoteFolderRepositoryImpl", "Error in Long", e)
        throw e
    }

    override suspend fun updateFolder(folder: NoteFolder) {
        try {
            folderDao.update(folder.toEntity())
        } catch (e: Exception) {
            Log.e("NoteFolderRepositoryImpl", "Error in NoteFolder", e)
            throw e
        }
    }

    override suspend fun deleteFolder(id: Long) {
        try {
            folderDao.deleteById(id)
        } catch (e: Exception) {
            Log.e("NoteFolderRepositoryImpl", "Error in Long", e)
            throw e
        }
    }

    override fun getAllFolders(): Flow<List<NoteFolder>> = try {
        folderDao.getAll().map { list -> list.map { it.toDomain() } }
    } catch (e: Exception) {
        Log.e("NoteFolderRepositoryImpl", "Error in Flow<List<NoteFolder>>", e)
        throw e
    }
}
