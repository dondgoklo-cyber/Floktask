package com.taskmanager.domain.usecase.finance

import com.taskmanager.domain.model.Account
import com.taskmanager.domain.repository.AccountRepository
import javax.inject.Inject
import com.taskmanager.domain.logger.Logger

class CreateAccountUseCase @Inject constructor( 
    private val logger: Logger,
    private val repository: AccountRepository
) {
    suspend operator fun invoke(account: Account): Long = runCatching {
        repository.createAccount(account)
    }.onFailure { e ->
        logger.error("CreateAccountUseCase", "Error in invoke", e)
    }.getOrThrow()
}
