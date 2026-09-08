package com.taskmanager.presentation.screens.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.model.Account
import com.taskmanager.domain.model.Budget
import com.taskmanager.domain.model.Category
import com.taskmanager.domain.model.CategoryType
import com.taskmanager.domain.model.Goal
import com.taskmanager.domain.model.Transaction
import com.taskmanager.domain.model.TransactionType
import com.taskmanager.domain.finance.ExchangeRateProvider
import com.taskmanager.domain.usecase.finance.CreateGoalUseCase
import com.taskmanager.domain.usecase.finance.CreateTransactionUseCase
import com.taskmanager.domain.usecase.finance.DeleteGoalUseCase
import com.taskmanager.domain.usecase.finance.DeleteTransactionUseCase
import com.taskmanager.domain.usecase.finance.GetAccountsUseCase
import com.taskmanager.domain.usecase.finance.GetAllBudgetsUseCase
import com.taskmanager.domain.usecase.finance.GetAllGoalsUseCase
import com.taskmanager.domain.usecase.finance.GetAllTransactionsUseCase
import com.taskmanager.domain.usecase.finance.GetCategoriesUseCase
import com.taskmanager.domain.usecase.finance.GetFinanceSummaryUseCase
import com.taskmanager.domain.usecase.finance.UpdateTransactionUseCase
import com.taskmanager.domain.usecase.finance.UpsertBudgetUseCase
import com.taskmanager.domain.usecase.finance.DeleteBudgetUseCase
import com.taskmanager.domain.usecase.settings.UserPreferences
import com.taskmanager.utils.divideSafe
import com.taskmanager.utils.sumOfBigDecimal
import com.taskmanager.utils.toMoneyBigDecimal
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
    private val getAccountsUseCase: GetAccountsUseCase,
    private val getAllGoalsUseCase: GetAllGoalsUseCase,
    private val getAllBudgetsUseCase: GetAllBudgetsUseCase,
    private val createTransactionUseCase: CreateTransactionUseCase,
    private val updateTransactionUseCase: UpdateTransactionUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val createGoalUseCase: CreateGoalUseCase,
    private val deleteGoalUseCase: DeleteGoalUseCase,
    private val upsertBudgetUseCase: UpsertBudgetUseCase,
    private val deleteBudgetUseCase: DeleteBudgetUseCase,
    private val exchangeRateProvider: ExchangeRateProvider,
    private val userPreferences: com.taskmanager.domain.usecase.settings.UserPreferences,
    private val logger: Logger
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
        viewModelScope.launch {
            try {
                getAllGoalsUseCase().collect { _goals.value = it }
            } catch (e: Exception) {
                logger.error("FinanceViewModel", "Error loading goals", e)
            }
        }
        viewModelScope.launch {
            try {
                getAllBudgetsUseCase().collect { _budgets.value = it }
            } catch (e: Exception) {
                logger.error("FinanceViewModel", "Error loading budgets", e)
            }
        }
        viewModelScope.launch {
            try {
                getAllTransactionsUseCase().collect { _allTransactions.value = it }
            } catch (e: Exception) {
                logger.error("FinanceViewModel", "Error loading transactions", e)
            }
        }
        viewModelScope.launch {
            try {
                getFinanceSummaryUseCase.totalIncome().collect { _totalIncome.value = it }
            } catch (e: Exception) {
                logger.error("FinanceViewModel", "Error loading total income", e)
            }
        }
        viewModelScope.launch {
            try {
                getFinanceSummaryUseCase.totalExpense().collect { _totalExpense.value = it }
            } catch (e: Exception) {
                logger.error("FinanceViewModel", "Error loading total expense", e)
            }
        }
        viewModelScope.launch {
            try {
                getFinanceSummaryUseCase.totalIncomeByCurrency().collect { _incomeByCurrency.value = it }
            } catch (e: Exception) {
                logger.error("FinanceViewModel", "Error loading income by currency", e)
            }
        }
        viewModelScope.launch {
            try {
                getFinanceSummaryUseCase.totalExpenseByCurrency().collect { _expenseByCurrency.value = it }
            } catch (e: Exception) {
                logger.error("FinanceViewModel", "Error loading expense by currency", e)
            }
        }
    }

    val state: StateFlow<FinanceUiState> = combine(
        _selectedPeriod,
        getCategoriesUseCase.all(),
        getAccountsUseCase()
    ) { period, categories, accounts ->
        val transactions = _allTransactions.value
        val totalIncome = _totalIncome.value
        val totalExpense = _totalExpense.value
        val baseCurrency = userPreferences.baseCurrency

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
        val zone = ZoneId.of("UTC")
        val (from, to) = periodRange(period, zone)
        val periodTx = transactions.filter { tx ->
            val txDate = tx.date.atZone(zone).toLocalDate()
            !txDate.isBefore(from) && !txDate.isAfter(to)
        }
        val periodIncome = periodTx.filter { it.type == TransactionType.INCOME }.sumOfBigDecimal { it.amount }.toDouble()
        val periodExpense = periodTx.filter { it.type == TransactionType.EXPENSE }.sumOfBigDecimal { it.amount }.toDouble()
        val net = periodIncome - periodExpense
        val balance = totalIncome - totalExpense

        val grouped = groupByDateLabel(periodTx, zone)
        val catExpenses = buildCategoryExpenses(periodTx, categories)

        // Daily balances (cumulative)
        val dailyBalances = buildDailyBalances(periodTx, zone)

        // Analytics
        val largestExpense = periodTx.filter { it.type == TransactionType.EXPENSE }
            .maxByOrNull { it.amount.toDouble() }
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
            .maxByOrNull { it.value.sumOfBigDecimal { tx -> tx.amount }.toDouble() }
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
                    amount = amount.toMoneyBigDecimal(),
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
            try {
                createGoalUseCase(Goal(title = title, targetAmount = targetAmount.toMoneyBigDecimal(), currency = currency))
            } catch (e: Exception) {
                logger.error("FinanceViewModel", "Error creating goal", e)
            }
        }
    }

    fun deleteGoal(id: Long) {
        viewModelScope.launch {
            try {
                deleteGoalUseCase(id)
            } catch (e: Exception) {
                logger.error("FinanceViewModel", "Error deleting goal", e)
            }
        }
    }

    fun setBudget(categoryId: Long, amount: Double, currency: String) {
        viewModelScope.launch {
            try {
                upsertBudgetUseCase(Budget(categoryId = categoryId, amount = amount.toMoneyBigDecimal(), currency = currency))
            } catch (e: Exception) {
                logger.error("FinanceViewModel", "Error setting budget", e)
            }
        }
    }

    fun deleteBudget(id: Long) {
        viewModelScope.launch {
            try {
                deleteBudgetUseCase(id)
            } catch (e: Exception) {
                logger.error("FinanceViewModel", "Error deleting budget", e)
            }
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
                TransactionType.INCOME -> running += tx.amount.toDouble()
                TransactionType.EXPENSE -> running -= tx.amount.toDouble()
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
            .groupBy { it.categoryId }
            .mapNotNull { (catId, txs) ->
                val cat = categories.find { it.id == catId }
                cat?.let { category ->
                    CategoryExpense(
                        categoryName = category.name ?: "Без категории",
                        categoryColor = category.color,
                        total = txs.sumOfBigDecimal { it.amount }.toDouble()
                    )
                }
            }
            .sortedByDescending { it.total }
    }
}

