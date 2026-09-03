package com.taskmanager.domain.usecase.finance

import com.taskmanager.domain.model.Transaction
import com.taskmanager.domain.repository.TransactionRepository
import javax.inject.Inject
import com.taskmanager.domain.logger.Logger

class UpdateTransactionUseCase @Inject constructor( 
    private val logger: Logger,
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transaction: Transaction) = runCatching {
        repository.updateTransaction(transaction)
    }.onFailure { e ->
        logger.error("UpdateTransactionUseCase", "Error in invoke", e)
    }
}
