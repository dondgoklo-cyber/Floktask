package com.taskmanager.domain.usecase.finance

import com.taskmanager.domain.model.Account
import com.taskmanager.domain.repository.AccountRepository
import javax.inject.Inject
import android.util.Log

class CreateAccountUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    suspend operator fun invoke(account: Account): Long = runCatching {
        repository.createAccount(account)
    }.onFailure { e ->
        Log.e("CreateAccountUseCase", "Error in invoke", e)
    }
}
