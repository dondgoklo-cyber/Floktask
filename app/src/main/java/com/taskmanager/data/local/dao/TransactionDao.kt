package com.taskmanager.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.taskmanager.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity): Long

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Delete
    suspend fun delete(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAll(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE date BETWEEN :from AND :to ORDER BY date DESC")
    fun getForPeriod(from: Long, to: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY date DESC LIMIT :limit")
    fun getRecent(limit: Int): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: Long): TransactionEntity?

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: Long)

    // TRANSFER excluded from income/expense calculations
    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'INCOME' AND date BETWEEN :from AND :to")
    fun getIncomeForPeriod(from: Long, to: Long): Flow<Double>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'EXPENSE' AND date BETWEEN :from AND :to")
    fun getExpenseForPeriod(from: Long, to: Long): Flow<Double>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'INCOME'")
    fun getTotalIncome(): Flow<Double>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'EXPENSE'")
    fun getTotalExpense(): Flow<Double>

    // Per-currency queries for multi-currency dashboard
    @Query("SELECT currency, COALESCE(SUM(amount), 0) as total FROM transactions WHERE type = 'INCOME' GROUP BY currency")
    fun getTotalIncomeByCurrency(): Flow<List<CurrencyTotal>>

    @Query("SELECT currency, COALESCE(SUM(amount), 0) as total FROM transactions WHERE type = 'EXPENSE' GROUP BY currency")
    fun getTotalExpenseByCurrency(): Flow<List<CurrencyTotal>>

    @Query("SELECT DISTINCT currency FROM transactions")
    fun getUsedCurrencies(): Flow<List<String>>
}

data class CurrencyTotal(
    val currency: String,
    val total: Double
)
