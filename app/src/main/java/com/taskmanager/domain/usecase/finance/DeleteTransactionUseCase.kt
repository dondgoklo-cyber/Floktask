package com.taskmanager.domain.usecase.finance

import com.taskmanager.domain.repository.TransactionRepository
import javax.inject.Inject
import com.taskmanager.domain.logger.Logger

class DeleteTransactionUseCase @Inject constructor( 
    private val logger: Logger,
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(id: Long) = runCatching {
        repository.deleteTransaction(id)
    }.onFailure { e ->
        logger.error("DeleteTransactionUseCase", "Error in invoke", e)
    }
}
