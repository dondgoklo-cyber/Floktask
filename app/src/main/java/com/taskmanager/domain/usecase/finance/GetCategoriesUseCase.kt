package com.taskmanager.domain.usecase.finance

import com.taskmanager.domain.model.CategoryType
import com.taskmanager.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    fun all(): Flow<List<com.taskmanager.domain.model.Category>> = repository.getAllCategories()
    fun byType(type: CategoryType): Flow<List<com.taskmanager.domain.model.Category>> =
        repository.getCategoriesByType(type)
}
