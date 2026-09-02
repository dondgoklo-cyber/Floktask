package com.taskmanager.presentation.screens.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.model.Account
import com.taskmanager.domain.model.Budget
import com.taskmanager.domain.model.Goal
import com.taskmanager.domain.model.Category
import com.taskmanager.domain.model.CategoryType
import com.taskmanager.domain.model.Transaction
import com.taskmanager.domain.model.TransactionType
import com.taskmanager.domain.repository.AccountRepository
import com.taskmanager.domain.repository.BudgetRepository
import com.taskmanager.domain.repository.GoalRepository
import com.taskmanager.domain.usecase.finance.CreateTransactionUseCase
import com.taskmanager.domain.usecase.finance.DeleteTransactionUseCase
import com.taskmanager.domain.usecase.finance.GetAccountsUseCase
import com.taskmanager.domain.usecase.finance.GetCategoriesUseCase
import com.taskmanager.domain.usecase.finance.GetAllTransactionsUseCase
import com.taskmanager.domain.finance.ExchangeRateProvider
import com.taskmanager.domain.usecase.finance.GetFinanceSummaryUseCase
import com.taskmanager.data.local.dao.CurrencyTotal
import android.app.Application
import com.taskmanager.security.UserPrefs
import com.taskmanager.domain.usecase.finance.UpdateTransactionUseCase
import com.taskmanager.util.HapticManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

enum class FinancePeriod { TODAY, WEEK, MONTH, YEAR }

data class TransactionGroup(
    val dateLabel: String,
    val transactions: List<Transaction>
)

data class CategoryExpense(
    val categoryName: String,
    val categoryColor: String?,
    val total: Double
)

data class AccountBalance(
    val currency: String,
    val balance: Double,
    val convertedBalance: Double
)

data class FinanceUiState(
    val balance: Double = 0.0,
    val balanceInBaseCurrency: Double = 0.0,
    val balancesByCurrency: List<AccountBalance> = emptyList(),
    val baseCurrency: String = "RUB",
    val periodIncome: Double = 0.0,
    val periodExpense: Double = 0.0,
    val net: Double = 0.0,
    val transactions: List<Transaction> = emptyList(),
    val groupedTransactions: List<TransactionGroup> = emptyList(),
    val categoryExpenses: List<CategoryExpense> = emptyList(),
    val largestExpense: Transaction? = null,
    val avgDailySpending: Double = 0.0,
    val avgMonthlySpending: Double = 0.0,
    val topIncomeSource: String? = null,
    val savingsRate: Double = 0.0,
    val budgets: List<Pair<Category, Budget>> = emptyList(),
    val goals: List<Goal> = emptyList(),
    val dailyBalances: List<Double> = emptyList(),
    val accounts: List<Account> = emptyList(),
    val categories: List<Category> = emptyList(),
    val currency: String = "RUB",
    val isLoading: Boolean = true
)

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class FinanceViewModel @Inject constructor(
    private val getAllTransactionsUseCase: GetAllTransactionsUseCase,
    private val getFinanceSummaryUseCase: GetFinanceSummaryUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    getAccountsUseCase: GetAccountsUseCase,
    private val createTransactionUseCase: CreateTransactionUseCase,
    private val updateTransactionUseCase: UpdateTransactionUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val accountRepository: AccountRepository,
    private val budgetRepository: BudgetRepository,
    private val goalRepository: GoalRepository,
    private val exchangeRateProvider: ExchangeRateProvider,
    private val app: Application,
    val hapticManager: HapticManager
) : ViewModel() {

    private val _selectedPeriod = MutableStateFlow(FinancePeriod.MONTH)
    val selectedPeriod: StateFlow<FinancePeriod> = _selectedPeriod.asStateFlow()

    private val _goals = MutableStateFlow<List<Goal>>(emptyList())
    private val _budgets = MutableStateFlow<List<Budget>>(emptyList())
    private val _allTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    private val _totalIncome = MutableStateFlow(0.0)
    private val _totalExpense = MutableStateFlow(0.0)
    private val _incomeByCurrency = MutableStateFlow<List<com.taskmanager.data.local.dao.CurrencyTotal>>(emptyList())
    private val _expenseByCurrency = MutableStateFlow<List<com.taskmanager.data.local.dao.CurrencyTotal>>(emptyList())

    init {
        viewModelScope.launch { goalRepository.getAllGoals().collect { _goals.value = it } }
        viewModelScope.launch { budgetRepository.getAllBudgets().collect { _budgets.value = it } }
        viewModelScope.launch { getAllTransactionsUseCase().collect { _allTransactions.value = it } }
        viewModelScope.launch { getFinanceSummaryUseCase.totalIncome().collect { _totalIncome.value = it } }
        viewModelScope.launch { getFinanceSummaryUseCase.totalExpense().collect { _totalExpense.value = it } }
        viewModelScope.launch { getFinanceSummaryUseCase.totalIncomeByCurrency().collect { _incomeByCurrency.value = it } }
        viewModelScope.launch { getFinanceSummaryUseCase.totalExpenseByCurrency().collect { _expenseByCurrency.value = it } }
    }

    val state: StateFlow<FinanceUiState> = combine(
        _selectedPeriod,
        getCategoriesUseCase.all(),
        getAccountsUseCase()
    ) { period, categories, accounts ->
        val transactions = _allTransactions.value
        val totalIncome = _totalIncome.value
        val totalExpense = _totalExpense.value
        val baseCurrency = UserPrefs(app).baseCurrency

        val incomeByCur = _incomeByCurrency.value.associate { it.currency to it.total }
        val expenseByCur = _expenseByCurrency.value.associate { it.currency to it.total }
        val allCurrencies = (incomeByCur.keys + expenseByCur.keys).distinct()
        val balancesByCurrency = allCurrencies.map { cur ->
            val bal = (incomeByCur[cur] ?: 0.0) - (expenseByCur[cur] ?: 0.0)
            AccountBalance(
                currency = cur,
                balance = bal,
                convertedBalance = exchangeRateProvider.convert(bal, cur, baseCurrency)
            )
        }
        val balanceInBaseCurrency = balancesByCurrency.sumOf { it.convertedBalance }
        val zone = ZoneId.systemDefault()
        val (from, to) = periodRange(period, zone)
        val periodTx = transactions.filter { tx ->
            val txDate = tx.date.atZone(zone).toLocalDate()
            !txDate.isBefore(from) && !txDate.isAfter(to)
        }
        val periodIncome = periodTx.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val periodExpense = periodTx.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        val net = periodIncome - periodExpense
        val balance = totalIncome - totalExpense

        val grouped = groupByDateLabel(periodTx, zone)
        val catExpenses = buildCategoryExpenses(periodTx, categories)

        // Daily balances (cumulative)
        val dailyBalances = buildDailyBalances(periodTx, zone)

        // Analytics
        val largestExpense = periodTx.filter { it.type == TransactionType.EXPENSE }
            .maxByOrNull { it.amount }
        val daysInPeriod = when (period) {
            FinancePeriod.TODAY -> 1
            FinancePeriod.WEEK -> 7
            FinancePeriod.MONTH -> 30
            FinancePeriod.YEAR -> 365
        }
        val avgDailySpending = if (daysInPeriod > 0) periodExpense / daysInPeriod else 0.0
        val avgMonthlySpending = avgDailySpending * 30
        val topIncomeSource = periodTx.filter { it.type == TransactionType.INCOME }
            .groupBy { it.categoryId }
            .maxByOrNull { it.value.sumOf { tx -> tx.amount } }
            ?.let { entry -> categories.find { it.id == entry.key }?.name }
        val savingsRate = if (periodIncome > 0) {
            ((periodIncome - periodExpense) / periodIncome * 100).coerceIn(0.0, 100.0)
        } else 0.0

        val goals = _goals.value
        val budgets = _budgets.value
        val budgetPairs = budgets.mapNotNull { budget ->
            categories.find { it.id == budget.categoryId }?.let { cat -> cat to budget }
        }

        val currency = accounts.firstOrNull()?.currency ?: "RUB"

        FinanceUiState(
            balance = balance,
            balanceInBaseCurrency = balanceInBaseCurrency,
            balancesByCurrency = balancesByCurrency,
            baseCurrency = baseCurrency,
            periodIncome = periodIncome,
            periodExpense = periodExpense,
            net = net,
            transactions = periodTx,
            groupedTransactions = grouped,
            categoryExpenses = catExpenses,
            largestExpense = largestExpense,
            avgDailySpending = avgDailySpending,
            avgMonthlySpending = avgMonthlySpending,
            topIncomeSource = topIncomeSource,
            savingsRate = savingsRate,
            budgets = budgetPairs,
            goals = goals,
            dailyBalances = dailyBalances,
            accounts = accounts,
            categories = categories,
            currency = currency,
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, FinanceUiState())

    fun selectPeriod(period: FinancePeriod) {
        _selectedPeriod.value = period
    }

    fun createTransaction(
        amount: Double,
        type: TransactionType,
        currency: String,
        categoryId: Long?,
        accountId: Long?,
        date: Instant,
        note: String?
    ) {
        viewModelScope.launch {
            createTransactionUseCase(
                Transaction(
                    amount = amount,
                    type = type,
                    currency = currency,
                    categoryId = categoryId,
                    accountId = accountId,
                    date = date,
                    note = note?.trim()?.ifBlank { null }
                )
            )
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            updateTransactionUseCase(transaction)
        }
    }

    fun createGoal(title: String, targetAmount: Double, currency: String) {
        viewModelScope.launch {
            goalRepository.createGoal(Goal(title = title, targetAmount = targetAmount, currency = currency))
        }
    }

    fun deleteGoal(id: Long) {
        viewModelScope.launch {
            goalRepository.deleteGoal(id)
        }
    }

    fun setBudget(categoryId: Long, amount: Double, currency: String) {
        viewModelScope.launch {
            budgetRepository.upsertBudget(Budget(categoryId = categoryId, amount = amount, currency = currency))
        }
    }

    fun deleteBudget(id: Long) {
        viewModelScope.launch {
            budgetRepository.deleteBudget(id)
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            deleteTransactionUseCase(id)
        }
    }

    private fun periodRange(period: FinancePeriod, zone: ZoneId): Pair<LocalDate, LocalDate> {
        val today = LocalDate.now()
        return when (period) {
            FinancePeriod.TODAY -> today to today
            FinancePeriod.WEEK -> today.minusDays(6) to today
            FinancePeriod.MONTH -> today.withDayOfMonth(1) to today
            FinancePeriod.YEAR -> today.withDayOfYear(1) to today
        }
    }

    private fun buildDailyBalances(
        transactions: List<Transaction>,
        zone: ZoneId
    ): List<Double> {
        val sorted = transactions.sortedBy { it.date }
        var running = 0.0
        val result = mutableListOf<Double>()
        var currentDate: LocalDate? = null
        
        for (tx in sorted) {
            val txDate = tx.date.atZone(zone).toLocalDate()
            if (currentDate != null && txDate != currentDate) {
                result.add(running)
            }
            currentDate = txDate
            when (tx.type) {
                TransactionType.INCOME -> running += tx.amount
                TransactionType.EXPENSE -> running -= tx.amount
                TransactionType.TRANSFER -> {}
            }
        }
        result.add(running)
        return result
    }

    private fun groupByDateLabel(
        transactions: List<Transaction>,
        zone: ZoneId
    ): List<TransactionGroup> {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        return transactions
            .groupBy { tx -> tx.date.atZone(zone).toLocalDate() }
            .toList()
            .sortedByDescending { it.first }
            .map { (date, txs) ->
                val label = when (date) {
                    today -> "Сегодня"
                    yesterday -> "Вчера"
                    else -> date.format(java.time.format.DateTimeFormatter.ofPattern("d MMMM"))
                }
                TransactionGroup(dateLabel = label, transactions = txs)
            }
    }

    private fun buildCategoryExpenses(
        transactions: List<Transaction>,
        categories: List<Category>
    ): List<CategoryExpense> {
        return transactions
            .filter { it.type == TransactionType.EXPENSE && it.categoryId != null }
            .groupBy { it.categoryId!! }
            .map { (catId, txs) ->
                val cat = categories.find { it.id == catId }
                CategoryExpense(
                    categoryName = cat?.name ?: "Без категории",
                    categoryColor = cat?.color,
                    total = txs.sumOf { it.amount }
                )
            }
            .sortedByDescending { it.total }
    }
}



    /**
     * Handle FAB long-press for Finance screen
     */
    fun onFabLongClick() {
        hapticManager.mediumVibrate()
    }
}
