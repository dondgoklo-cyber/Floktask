package com.taskmanager.domain.usecase.finance

import com.taskmanager.domain.repository.CategoryRepository
import javax.inject.Inject
import com.taskmanager.domain.logger.Logger

class DeleteCategoryUseCase @Inject constructor( 
    private val logger: Logger,
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(id: Long) = runCatching {
        repository.deleteCategory(id)
    }.onFailure { e ->
        logger.error("DeleteCategoryUseCase", "Error in invoke", e)
    }
}
