package com.taskmanager.data.repository

import com.taskmanager.data.local.dao.NoteFolderDao
import com.taskmanager.domain.model.NoteFolder
import com.taskmanager.domain.repository.NoteFolderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NoteFolderRepositoryImpl @Inject constructor(
    private val folderDao: NoteFolderDao
) : NoteFolderRepository {

    override suspend fun createFolder(folder: NoteFolder): Long =
        folderDao.insert(folder.toEntity())

    override suspend fun updateFolder(folder: NoteFolder) {
        folderDao.update(folder.toEntity())
    }

    override suspend fun deleteFolder(id: Long) {
        folderDao.deleteById(id)
    }

    override fun getAllFolders(): Flow<List<NoteFolder>> =
        folderDao.getAll().map { list -> list.map { it.toDomain() } }
}
