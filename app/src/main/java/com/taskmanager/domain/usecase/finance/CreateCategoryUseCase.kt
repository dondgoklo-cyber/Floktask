package com.taskmanager.domain.usecase.finance

import com.taskmanager.domain.model.Category
import com.taskmanager.domain.repository.CategoryRepository
import javax.inject.Inject
import android.util.Log

class CreateCategoryUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(category: Category): Long = runCatching {
        repository.createCategory(category)
    }.onFailure { e ->
        Log.e("CreateCategoryUseCase", "Error in invoke", e)
    }
}
