package com.taskmanager.domain.usecase.finance

import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.model.Account
import com.taskmanager.domain.repository.AccountRepository
import javax.inject.Inject

class CreateAccountUseCase @Inject constructor(
    private val repository: AccountRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(account: Account): Long = runCatching {
        repository.createAccount(account)
    }.onFailure { e ->
        logger.error("CreateAccountUseCase", "Error in invoke", e)
    }.getOrThrow()
}
