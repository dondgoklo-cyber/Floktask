package com.taskmanager.domain.usecase.finance

import com.taskmanager.domain.repository.CategoryRepository
import javax.inject.Inject
import android.util.Log

class DeleteCategoryUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(id: Long) = runCatching {
        repository.deleteCategory(id)
    }.onFailure { e ->
        Log.e("DeleteCategoryUseCase", "Error in invoke", e)
    }
}
