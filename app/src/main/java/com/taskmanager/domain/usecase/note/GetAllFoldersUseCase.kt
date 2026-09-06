package com.taskmanager.domain.usecase.note

import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.model.NoteFolder
import com.taskmanager.domain.repository.NoteFolderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving all note folders.
 * Part of Clean Architecture - Presentation layer depends only on UseCases, not Repositories.
 */
class GetAllFoldersUseCase @Inject constructor(
    private val noteFolderRepository: NoteFolderRepository,
    private val logger: Logger
) {
    operator fun invoke(): Flow<List<NoteFolder>> = noteFolderRepository.getAllFolders()
}
