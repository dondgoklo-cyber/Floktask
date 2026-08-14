package com.taskmanager.data.repository

import com.taskmanager.data.local.dao.AccountDao
import com.taskmanager.domain.model.Account
import com.taskmanager.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val accountDao: AccountDao
) : AccountRepository {

    override suspend fun createAccount(account: Account): Long =
        accountDao.insert(account.toEntity())

    override suspend fun updateAccount(account: Account) {
        accountDao.update(account.toEntity())
    }

    override suspend fun deleteAccount(id: Long) {
        accountDao.deleteById(id)
    }

    override fun getAllAccounts(): Flow<List<Account>> =
        accountDao.getAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getAccountCount(): Int = accountDao.count()
}
