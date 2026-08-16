package com.taskmanager.domain.usecase.finance

import com.taskmanager.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFinanceSummaryUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    /** Возвращает [totalIncome, totalExpense] как Flow для расчёта баланса. */
    fun totalIncome(): Flow<Double> = repository.getTotalIncome()
    fun totalExpense(): Flow<Double> = repository.getTotalExpense()
    fun totalIncomeByCurrency(): Flow<List<com.taskmanager.data.local.dao.CurrencyTotal>> = repository.getTotalIncomeByCurrency()
    fun totalExpenseByCurrency(): Flow<List<com.taskmanager.data.local.dao.CurrencyTotal>> = repository.getTotalExpenseByCurrency()

    fun incomeForPeriod(from: Long, to: Long): Flow<Double> =
        repository.getIncomeForPeriod(from, to)

    fun expenseForPeriod(from: Long, to: Long): Flow<Double> =
        repository.getExpenseForPeriod(from, to)
}
