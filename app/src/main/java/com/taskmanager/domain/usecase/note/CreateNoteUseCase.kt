package com.taskmanager.domain.usecase.note

import com.taskmanager.domain.model.Note
import com.taskmanager.domain.repository.NoteRepository
import javax.inject.Inject
import com.taskmanager.domain.logger.Logger

class CreateNoteUseCase @Inject constructor( 
    private val logger: Logger,
    private val repository: NoteRepository
) {
    suspend operator fun invoke(note: Note): Long = runCatching {
        repository.createNote(note)
    }.onFailure { e ->
        logger.error("CreateNoteUseCase", "Error in invoke", e)
    }.getOrThrow()
}
