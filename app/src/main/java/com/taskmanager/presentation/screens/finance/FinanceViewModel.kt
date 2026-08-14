package com.taskmanager.presentation.screens.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.model.Account
import com.taskmanager.domain.model.Category
import com.taskmanager.domain.model.CategoryType
import com.taskmanager.domain.model.Transaction
import com.taskmanager.domain.model.TransactionType
import com.taskmanager.domain.repository.AccountRepository
import com.taskmanager.domain.usecase.finance.CreateTransactionUseCase
import com.taskmanager.domain.usecase.finance.DeleteTransactionUseCase
import com.taskmanager.domain.usecase.finance.GetAccountsUseCase
import com.taskmanager.domain.usecase.finance.GetCategoriesUseCase
import com.taskmanager.domain.usecase.finance.GetAllTransactionsUseCase
import com.taskmanager.domain.usecase.finance.GetFinanceSummaryUseCase
import com.taskmanager.domain.usecase.finance.UpdateTransactionUseCase
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

data class FinanceUiState(
    val balance: Double = 0.0,
    val periodIncome: Double = 0.0,
    val periodExpense: Double = 0.0,
    val net: Double = 0.0,
    val transactions: List<Transaction> = emptyList(),
    val groupedTransactions: List<TransactionGroup> = emptyList(),
    val categoryExpenses: List<CategoryExpense> = emptyList(),
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
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _selectedPeriod = MutableStateFlow(FinancePeriod.MONTH)
    val selectedPeriod: StateFlow<FinancePeriod> = _selectedPeriod.asStateFlow()

    val state: StateFlow<FinanceUiState> = combine(
        _selectedPeriod,
        getAllTransactionsUseCase(),
        getFinanceSummaryUseCase.totalIncome(),
        getFinanceSummaryUseCase.totalExpense(),
        getCategoriesUseCase.all(),
        getAccountsUseCase()
    ) { period, transactions, totalIncome, totalExpense, categories, accounts ->
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

        val currency = accounts.firstOrNull()?.currency ?: "RUB"

        FinanceUiState(
            balance = balance,
            periodIncome = periodIncome,
            periodExpense = periodExpense,
            net = net,
            transactions = periodTx,
            groupedTransactions = grouped,
            categoryExpenses = catExpenses,
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
