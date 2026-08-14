package com.taskmanager.data.repository

import com.taskmanager.data.local.dao.TransactionDao
import com.taskmanager.domain.model.Transaction
import com.taskmanager.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao
) : TransactionRepository {

    override suspend fun createTransaction(transaction: Transaction): Long =
        transactionDao.insert(transaction.toEntity())

    override suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.update(transaction.toEntity())
    }

    override suspend fun deleteTransaction(id: Long) {
        transactionDao.deleteById(id)
    }

    override fun getAllTransactions(): Flow<List<Transaction>> =
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
}
