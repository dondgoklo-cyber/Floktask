package com.taskmanager.domain.usecase.finance

import android.util.Log
import com.taskmanager.domain.model.Transaction
import com.taskmanager.domain.repository.TransactionRepository
import javax.inject.Inject

class CreateTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transaction: Transaction): Result<Long> = runCatching {
        repository.createTransaction(transaction)
    }.onFailure { e ->
        Log.e("CreateTransactionUseCase", "Error creating transaction", e)
    }
}
