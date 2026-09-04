package com.taskmanager.data.repository
import com.taskmanager.domain.logger.Logger

import com.taskmanager.data.local.dao.NoteDao
import com.taskmanager.domain.model.Note
import com.taskmanager.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val logger: Logger,
    private val noteDao: NoteDao
) : NoteRepository {

    override suspend fun createNote(note: Note): Long = try {
        noteDao.insert(note.toEntity())
    } catch (e: Exception) {
        logger.error("NoteRepositoryImpl", "Error in Long", e)
        throw e
    }

    override suspend fun updateNote(note: Note) {
        try {
            noteDao.update(note.toEntity())
        } catch (e: Exception) {
            logger.error("NoteRepositoryImpl", "Error in Note", e)
            throw e
        }
    }

    override suspend fun deleteNote(id: Long) {
        try {
            noteDao.deleteById(id)
        } catch (e: Exception) {
            logger.error("NoteRepositoryImpl", "Error in Long", e)
            throw e
        }
    }

    override fun getAllNotes(): Flow<List<Note>> = try {
        noteDao.getAll().map { list -> list.map { it.toDomain() } }
    } catch (e: Exception) {
        logger.error("NoteRepositoryImpl", "Error in Flow<List<Note>>", e)
        throw e
    }

    override fun getPinnedNotes(): Flow<List<Note>> =
        noteDao.getPinned().map { list -> list.map { it.toDomain() } }

    override fun getNotesByFolder(folderId: Long): Flow<List<Note>> =
        noteDao.getByFolder(folderId).map { list -> list.map { it.toDomain() } }

    override fun getNotesByProject(projectId: Long): Flow<List<Note>> =
        noteDao.getByProject(projectId).map { list -> list.map { it.toDomain() } }

    override suspend fun getNoteById(id: Long): Note? = try {
        noteDao.getById(id)?.toDomain()
    } catch (e: Exception) {
        logger.error("NoteRepositoryImpl", "Error in Note?", e)
        throw e
    }

    override fun searchNotes(query: String): Flow<List<Note>> =
        noteDao.search(query).map { list -> list.map { it.toDomain() } }

    override suspend fun setPinned(id: Long, pinned: Boolean) {
        try {
            noteDao.setPinned(id, pinned, System.currentTimeMillis())
        } catch (e: Exception) {
            logger.error("NoteRepositoryImpl", "Error in Boolean", e)
            throw e
        }
    }

    override suspend fun setArchived(id: Long, archived: Boolean) {
        try {
            noteDao.setArchived(id, archived, System.currentTimeMillis())
        } catch (e: Exception) {
            logger.error("NoteRepositoryImpl", "Error in Boolean", e)
            throw e
        }
    }

    override suspend fun moveToFolder(id: Long, folderId: Long?) {
        try {
            noteDao.moveToFolder(id, folderId, System.currentTimeMillis())
        } catch (e: Exception) {
            logger.error("NoteRepositoryImpl", "Error in moveToFolder", e)
            throw e
        }
    }
}
