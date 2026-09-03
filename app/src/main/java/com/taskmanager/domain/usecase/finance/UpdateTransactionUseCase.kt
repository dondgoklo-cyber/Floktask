package com.taskmanager.domain.usecase.finance

import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.model.Transaction
import com.taskmanager.domain.repository.TransactionRepository
import javax.inject.Inject

class UpdateTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(transaction: Transaction) = runCatching {
        repository.updateTransaction(transaction)
    }.onFailure { e ->
        logger.error("UpdateTransactionUseCase", "Error in invoke", e)
    }
}
