package com.taskmanager.domain.repository

import com.taskmanager.domain.model.Transaction
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface TransactionRepository {
    suspend fun createTransaction(transaction: Transaction): Long
    suspend fun updateTransaction(transaction: Transaction)
    suspend fun deleteTransaction(id: Long)

    fun getAllTransactions(): Flow<List<Transaction>>
    fun getTransactionsForPeriod(fromEpoch: Long, toEpoch: Long): Flow<List<Transaction>>
    fun getRecentTransactions(limit: Int): Flow<List<Transaction>>

    fun getIncomeForPeriod(fromEpoch: Long, toEpoch: Long): Flow<BigDecimal>
    fun getExpenseForPeriod(fromEpoch: Long, toEpoch: Long): Flow<BigDecimal>
    fun getTotalIncome(): Flow<BigDecimal>
    fun getTotalExpense(): Flow<BigDecimal>

    fun getTotalIncomeByCurrency(): Flow<List<com.taskmanager.data.local.dao.CurrencyTotal>>
    fun getTotalExpenseByCurrency(): Flow<List<com.taskmanager.data.local.dao.CurrencyTotal>>
}
