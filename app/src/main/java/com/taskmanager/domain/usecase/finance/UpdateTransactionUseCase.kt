package com.taskmanager.domain.usecase.finance

import com.taskmanager.domain.model.Transaction
import com.taskmanager.domain.repository.TransactionRepository
import javax.inject.Inject
import android.util.Log

class UpdateTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transaction: Transaction) = runCatching {
        repository.updateTransaction(transaction)
    }.onFailure { e ->
        Log.e("UpdateTransactionUseCase", "Error in invoke", e)
    }
}
