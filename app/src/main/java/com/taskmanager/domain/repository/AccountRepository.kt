package com.taskmanager.domain.repository

import com.taskmanager.domain.model.Account
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    suspend fun createAccount(account: Account): Long
    suspend fun updateAccount(account: Account)
    suspend fun deleteAccount(id: Long)

    fun getAllAccounts(): Flow<List<Account>>
    suspend fun getAccountCount(): Int
}
