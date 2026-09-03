package com.taskmanager.data.repository

import com.taskmanager.data.local.dao.AccountDao
import com.taskmanager.domain.model.Account
import com.taskmanager.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import android.util.Log

class AccountRepositoryImpl @Inject constructor(
    private val accountDao: AccountDao
) : AccountRepository {

    override suspend fun createAccount(account: Account): Long = try {
        accountDao.insert(account.toEntity())
    } catch (e: Exception) {
        Log.e("AccountRepositoryImpl", "Error in Long", e)
        throw e
    }

    override suspend fun updateAccount(account: Account) {
        try {
            accountDao.update(account.toEntity())
        } catch (e: Exception) {
            Log.e("AccountRepositoryImpl", "Error in Account", e)
            throw e
        }
    }

    override suspend fun deleteAccount(id: Long) {
        try {
            accountDao.deleteById(id)
        } catch (e: Exception) {
            Log.e("AccountRepositoryImpl", "Error in Long", e)
            throw e
        }
    }

    override fun getAllAccounts(): Flow<List<Account>> = try {
        accountDao.getAll().map { list -> list.map { it.toDomain() } }
    } catch (e: Exception) {
        Log.e("AccountRepositoryImpl", "Error in Flow<List<Account>>", e)
        throw e
    }

    override suspend fun getAccountCount(): Int = try {
        accountDao.count()
    } catch (e: Exception) {
        Log.e("AccountRepositoryImpl", "Error in Int", e)
        throw e
    }
}
