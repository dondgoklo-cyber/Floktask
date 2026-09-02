package com.taskmanager.data.repository

import com.taskmanager.data.local.dao.TransactionDao
import com.taskmanager.domain.model.Transaction
import com.taskmanager.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import android.util.Log

class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao
) : TransactionRepository {

    override suspend fun createTransaction(transaction: Transaction): Long = try {
        
    } catch (e: Exception) {
        Log.e("TransactionRepositoryImpl", "Error in Long", e)
        throw e
    }
        transactionDao.insert(transaction.toEntity())

    override suspend fun updateTransaction(transaction: Transaction) {
        try {
            transactionDao.update(transaction.toEntity())
        } catch (e: Exception) {
            Log.e("TransactionRepositoryImpl", "Error in Transaction)", e)
            throw e
        }
    }

    override suspend fun deleteTransaction(id: Long) {
        try {
            transactionDao.deleteById(id)
        } catch (e: Exception) {
            Log.e("TransactionRepositoryImpl", "Error in Long)", e)
            throw e
        }
    }

    override fun getAllTransactions(): Flow<List<Transaction>> = try {
        
    } catch (e: Exception) {
        Log.e("TransactionRepositoryImpl", "Error in Flow<List<Transaction>>", e)
        throw e
    }
        transactionDao.getAll().map { list -> list.map { it.toDomain() } }

    override fun getTransactionsForPeriod(fromEpoch: Long, toEpoch: Long): Flow<List<Transaction>> =
        transactionDao.getForPeriod(fromEpoch, toEpoch).map { list -> list.map { it.toDomain() } }

    override fun getRecentTransactions(limit: Int): Flow<List<Transaction>> =
        transactionDao.getRecent(limit).map { list -> list.map { it.toDomain() } }

    override fun getIncomeForPeriod(fromEpoch: Long, toEpoch: Long): Flow<Double> =
        transactionDao.getIncomeForPeriod(fromEpoch, toEpoch)

    override fun getExpenseForPeriod(fromEpoch: Long, toEpoch: Long): Flow<Double> =
        transactionDao.getExpenseForPeriod(fromEpoch, toEpoch)

    override fun getTotalIncome(): Flow<Double> =
        transactionDao.getTotalIncome()

    override fun getTotalExpense(): Flow<Double> =
        transactionDao.getTotalExpense()

    override fun getTotalIncomeByCurrency(): Flow<List<com.taskmanager.data.local.dao.CurrencyTotal>> =
        transactionDao.getTotalIncomeByCurrency()

    override fun getTotalExpenseByCurrency(): Flow<List<com.taskmanager.data.local.dao.CurrencyTotal>> =
        transactionDao.getTotalExpenseByCurrency()
}
