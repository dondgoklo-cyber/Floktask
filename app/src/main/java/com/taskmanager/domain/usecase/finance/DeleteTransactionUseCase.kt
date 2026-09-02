package com.taskmanager.domain.usecase.finance

import com.taskmanager.domain.repository.TransactionRepository
import javax.inject.Inject
import android.util.Log

class DeleteTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(id: Long) = runCatching {
        repository.deleteTransaction(id)
    }.onFailure { e ->
        Log.e("DeleteTransactionUseCase", "Error in invoke", e)
    }
}
