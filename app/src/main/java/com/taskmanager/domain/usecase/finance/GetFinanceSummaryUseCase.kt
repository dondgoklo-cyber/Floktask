package com.taskmanager.domain.usecase.finance

import com.taskmanager.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import javax.inject.Inject

class GetFinanceSummaryUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    /** Возвращает [totalIncome, totalExpense] как Flow для расчёта баланса. */
    fun totalIncome(): Flow<BigDecimal> = repository.getTotalIncome()
    fun totalExpense(): Flow<BigDecimal> = repository.getTotalExpense()
    fun totalIncomeByCurrency(): Flow<List<com.taskmanager.data.local.dao.CurrencyTotal>> = repository.getTotalIncomeByCurrency()
    fun totalExpenseByCurrency(): Flow<List<com.taskmanager.data.local.dao.CurrencyTotal>> = repository.getTotalExpenseByCurrency()

    fun incomeForPeriod(from: Long, to: Long): Flow<BigDecimal> =
        repository.getIncomeForPeriod(from, to)

    fun expenseForPeriod(from: Long, to: Long): Flow<BigDecimal> =
        repository.getExpenseForPeriod(from, to)
}
