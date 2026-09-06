package com.taskmanager.domain.usecase.note

import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.repository.NoteRepository
import javax.inject.Inject

/**
 * Use case for setting note pinned status.
 * Part of Clean Architecture - Presentation layer depends only on UseCases, not Repositories.
 */
class SetPinnedUseCase @Inject constructor(
    private val noteRepository: NoteRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(noteId: Long, pinned: Boolean) = runCatching {
        noteRepository.setPinned(noteId, pinned)
    }.onFailure { e ->
        logger.error("SetPinnedUseCase", "Error setting pinned status", e)
    }
}
