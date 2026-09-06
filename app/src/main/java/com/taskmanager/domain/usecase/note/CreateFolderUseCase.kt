package com.taskmanager.domain.usecase.note

import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.model.NoteFolder
import com.taskmanager.domain.repository.NoteFolderRepository
import javax.inject.Inject

/**
 * Use case for creating a note folder.
 * Part of Clean Architecture - Presentation layer depends only on UseCases, not Repositories.
 */
class CreateFolderUseCase @Inject constructor(
    private val noteFolderRepository: NoteFolderRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(folder: NoteFolder): Long = runCatching {
        noteFolderRepository.createFolder(folder)
    }.onFailure { e ->
        logger.error("CreateFolderUseCase", "Error creating folder", e)
    }.getOrThrow()
}
