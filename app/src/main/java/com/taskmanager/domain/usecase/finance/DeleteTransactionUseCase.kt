package com.taskmanager.domain.usecase.finance

import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.repository.TransactionRepository
import javax.inject.Inject

class DeleteTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(id: Long) = runCatching {
        repository.deleteTransaction(id)
    }.onFailure { e ->
        logger.error("DeleteTransactionUseCase", "Error in invoke", e)
    }
}
