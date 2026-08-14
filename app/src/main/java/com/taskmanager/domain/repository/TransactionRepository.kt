package com.taskmanager.domain.repository

import com.taskmanager.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    suspend fun createTransaction(transaction: Transaction): Long
    suspend fun updateTransaction(transaction: Transaction)
    suspend fun deleteTransaction(id: Long)

    fun getAllTransactions(): Flow<List<Transaction>>
    fun getTransactionsForPeriod(fromEpoch: Long, toEpoch: Long): Flow<List<Transaction>>
    fun getRecentTransactions(limit: Int): Flow<List<Transaction>>

    fun getIncomeForPeriod(fromEpoch: Long, toEpoch: Long): Flow<Double>
    fun getExpenseForPeriod(fromEpoch: Long, toEpoch: Long): Flow<Double>
    fun getTotalIncome(): Flow<Double>
    fun getTotalExpense(): Flow<Double>

    fun getTotalIncomeByCurrency(): Flow<List<com.taskmanager.data.local.dao.CurrencyTotal>>
    fun getTotalExpenseByCurrency(): Flow<List<com.taskmanager.data.local.dao.CurrencyTotal>>
}
