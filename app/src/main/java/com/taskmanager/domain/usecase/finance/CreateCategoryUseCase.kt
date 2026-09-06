package com.taskmanager.domain.usecase.finance

import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.model.Category
import com.taskmanager.domain.repository.CategoryRepository
import javax.inject.Inject

class CreateCategoryUseCase @Inject constructor(
    private val repository: CategoryRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(category: Category): Long = runCatching {
        repository.createCategory(category)
    }.onFailure { e ->
        logger.error("CreateCategoryUseCase", "Error in invoke", e)
    }.getOrThrow()
}
