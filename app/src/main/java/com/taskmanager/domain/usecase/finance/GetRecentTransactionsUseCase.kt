package com.taskmanager.domain.usecase.finance

import com.taskmanager.domain.model.Transaction
import com.taskmanager.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecentTransactionsUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(limit: Int = 3): Flow<List<Transaction>> =
        repository.getRecentTransactions(limit)
}
