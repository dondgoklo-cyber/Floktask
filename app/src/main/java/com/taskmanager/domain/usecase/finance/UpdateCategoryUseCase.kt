package com.taskmanager.domain.usecase.finance

import com.taskmanager.domain.model.Category
import com.taskmanager.domain.repository.CategoryRepository
import javax.inject.Inject
import android.util.Log

class UpdateCategoryUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(category: Category) = runCatching {
        repository.updateCategory(category)
    }.onFailure { e ->
        Log.e("UpdateCategoryUseCase", "Error in invoke", e)
    }
}
