package com.taskmanager.data.repository

import com.taskmanager.data.local.dao.NoteDao
import com.taskmanager.domain.model.Note
import com.taskmanager.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao
) : NoteRepository {

    override suspend fun createNote(note: Note): Long =
        noteDao.insert(note.toEntity())

    override suspend fun updateNote(note: Note) {
        noteDao.update(note.toEntity())
    }

    override suspend fun deleteNote(id: Long) {
        noteDao.deleteById(id)
    }

    override fun getAllNotes(): Flow<List<Note>> =
        noteDao.getAll().map { list -> list.map { it.toDomain() } }

    override fun getPinnedNotes(): Flow<List<Note>> =
        noteDao.getPinned().map { list -> list.map { it.toDomain() } }

    override fun getNotesByFolder(folderId: Long): Flow<List<Note>> =
        noteDao.getByFolder(folderId).map { list -> list.map { it.toDomain() } }

    override fun getNotesByProject(projectId: Long): Flow<List<Note>> =
        noteDao.getByProject(projectId).map { list -> list.map { it.toDomain() } }

    override suspend fun getNoteById(id: Long): Note? =
        noteDao.getById(id)?.toDomain()

    override fun searchNotes(query: String): Flow<List<Note>> =
        noteDao.search(query).map { list -> list.map { it.toDomain() } }

    override suspend fun setPinned(id: Long, pinned: Boolean) {
        noteDao.setPinned(id, pinned, System.currentTimeMillis())
    }

    override suspend fun setArchived(id: Long, archived: Boolean) {
        noteDao.setArchived(id, archived, System.currentTimeMillis())
    }

    override suspend fun moveToFolder(id: Long, folderId: Long?) {
        noteDao.moveToFolder(id, folderId, System.currentTimeMillis())
    }
}
