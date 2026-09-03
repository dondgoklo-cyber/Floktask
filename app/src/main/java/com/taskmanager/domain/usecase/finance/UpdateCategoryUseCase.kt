package com.taskmanager.domain.usecase.finance

import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.model.Category
import com.taskmanager.domain.repository.CategoryRepository
import javax.inject.Inject

class UpdateCategoryUseCase @Inject constructor(
    private val repository: CategoryRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(category: Category) = runCatching {
        repository.updateCategory(category)
    }.onFailure { e ->
        logger.error("UpdateCategoryUseCase", "Error in invoke", e)
    }
}
