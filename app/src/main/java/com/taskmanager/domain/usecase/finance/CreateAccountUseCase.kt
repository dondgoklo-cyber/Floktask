package com.taskmanager.domain.usecase.finance

import com.taskmanager.domain.model.Account
import com.taskmanager.domain.repository.AccountRepository
import javax.inject.Inject

class CreateAccountUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    suspend operator fun invoke(account: Account): Long = repository.createAccount(account)
}
