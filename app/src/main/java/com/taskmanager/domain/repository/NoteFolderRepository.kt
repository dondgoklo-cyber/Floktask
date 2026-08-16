package com.taskmanager.domain.repository

import com.taskmanager.domain.model.NoteFolder
import kotlinx.coroutines.flow.Flow

interface NoteFolderRepository {
    suspend fun createFolder(folder: NoteFolder): Long
    suspend fun updateFolder(folder: NoteFolder)
    suspend fun deleteFolder(id: Long)

    fun getAllFolders(): Flow<List<NoteFolder>>
}
