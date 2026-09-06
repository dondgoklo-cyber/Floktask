package com.taskmanager.domain.usecase.finance

import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.repository.CategoryRepository
import javax.inject.Inject

class DeleteCategoryUseCase @Inject constructor(
    private val repository: CategoryRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(id: Long) = runCatching {
        repository.deleteCategory(id)
    }.onFailure { e ->
        logger.error("DeleteCategoryUseCase", "Error in invoke", e)
    }
}
