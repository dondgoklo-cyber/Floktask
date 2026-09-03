package com.taskmanager.domain.usecase.finance

import com.taskmanager.domain.model.Category
import com.taskmanager.domain.repository.CategoryRepository
import javax.inject.Inject
import com.taskmanager.domain.logger.Logger

class UpdateCategoryUseCase @Inject constructor( 
    private val logger: Logger,
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(category: Category) = runCatching {
        repository.updateCategory(category)
    }.onFailure { e ->
        logger.error("UpdateCategoryUseCase", "Error in invoke", e)
    }
}
