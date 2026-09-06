package com.taskmanager.domain.usecase.note

import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.model.Note
import com.taskmanager.domain.repository.NoteRepository
import javax.inject.Inject

/**
 * Use case for retrieving a note by ID.
 * Part of Clean Architecture - Presentation layer depends only on UseCases, not Repositories.
 */
class GetNoteByIdUseCase @Inject constructor(
    private val noteRepository: NoteRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(id: Long): Note? = runCatching {
        noteRepository.getNoteById(id)
    }.onFailure { e ->
        logger.error("GetNoteByIdUseCase", "Error getting note by id", e)
    }.getOrNull()
}
