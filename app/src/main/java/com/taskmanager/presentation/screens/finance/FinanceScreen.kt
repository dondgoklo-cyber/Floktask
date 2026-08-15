package com.taskmanager.presentation.screens.finance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.taskmanager.R
import com.taskmanager.domain.model.Transaction
import com.taskmanager.domain.model.TransactionType
import com.taskmanager.presentation.components.AppFloatingActionButton
import com.taskmanager.presentation.components.EmptyState
import com.taskmanager.presentation.components.parseTagColor
import com.taskmanager.presentation.theme.AppTheme
import com.taskmanager.presentation.theme.Elevation
import com.taskmanager.presentation.theme.Radius
import com.taskmanager.data.repository.FinanceExportManager
import com.taskmanager.presentation.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceScreen(
    viewModel: FinanceViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()
    var showAddSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.finance),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = AppTheme.colors.surface
                )
            )
        },
        floatingActionButton = {
            AppFloatingActionButton(
                icon = Icons.Filled.Add,
                contentDescription = stringResource(R.string.add_transaction),
                onClick = { showAddSheet = true }
            )
        }
    ) { padding ->
        val context = LocalContext.current
        val exportManager = remember { FinanceExportManager() }
        val csvLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.CreateDocument("text/csv")
        ) { uri ->
            uri?.let {
                context.contentResolver.openOutputStream(it)?.use { stream ->
                    val writer = stream.bufferedWriter()
                    exportManager.exportToCsv(
                        state.transactions, state.categories, state.accounts, writer
                    )
                    Toast.makeText(context, "CSV экспортирован", Toast.LENGTH_SHORT).show()
                }
            }
        }
        val jsonLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.CreateDocument("application/json")
        ) { uri ->
            uri?.let {
                context.contentResolver.openOutputStream(it)?.use { stream ->
                    val json = exportManager.exportToJson(
                        state.transactions, state.categories, state.accounts
                    )
                    stream.write(json.toByteArray())
                    Toast.makeText(context, "JSON экспортирован", Toast.LENGTH_SHORT).show()
                }
            }
        }
        val importLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri ->
            uri?.let {
                context.contentResolver.openInputStream(it)?.use { stream ->
                    val json = stream.bufferedReader().readText()
                    val imported = exportManager.importFromJson(json)
                    imported.forEach { tx ->
                        viewModel.createTransaction(
                            tx.amount, tx.type, tx.currency, tx.categoryId,
                            tx.accountId, tx.date, tx.note
                        )
                    }
                    Toast.makeText(context, "Импортировано: ${imported.size} операций", Toast.LENGTH_SHORT).show()
                }
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            // Balance card
            item {
                BalanceCard(
                    balance = state.balanceInBaseCurrency,
                    currency = state.baseCurrency,
                    balancesByCurrency = state.balancesByCurrency
                )
            }

            // Period selector
            item {
                PeriodSelector(
                    selected = selectedPeriod,
                    onSelect = viewModel::selectPeriod
                )
            }

            // Period summary
            item {
                PeriodSummary(
                    income = state.periodIncome,
                    expense = state.periodExpense,
                    net = state.net,
                    currency = state.currency
                )
            }

            // Category breakdown (expenses)
            if (state.categoryExpenses.isNotEmpty()) {
                item {
                    CategoryBreakdown(
                        expenses = state.categoryExpenses,
                        currency = state.currency
                    )
                }
            }

            // Analytics
            if (state.periodExpense > 0 || state.periodIncome > 0) {
                item {
                    AnalyticsCard(state)
                }
            }

            // Export buttons
            if (state.transactions.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        TextButton(onClick = {
                            csvLauncher.launch(exportManager.generateFileName("wolftask_finance", "csv"))
                        }) { Text("📊 CSV") }
                        TextButton(onClick = {
                            jsonLauncher.launch(exportManager.generateFileName("wolftask_finance", "json"))
                        }) { Text("📦 JSON") }
                        TextButton(onClick = {
                            importLauncher.launch(arrayOf("application/json", "text/plain", "*/*"))
                        }) { Text("📥 Импорт") }
                    }
                }
            }

            // Transactions
            if (state.groupedTransactions.isNotEmpty()) {
                item {
                    Text(
                        stringResource(R.string.transactions),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = Spacing.sm)
                    )
                }
                state.groupedTransactions.forEach { group ->
                    item(key = "header-${group.dateLabel}") {
                        Text(
                            group.dateLabel,
                            style = MaterialTheme.typography.labelLarge,
                            color = AppTheme.colors.onSurfaceVariant,
                            modifier = Modifier.padding(top = Spacing.sm, bottom = Spacing.xs)
                        )
                    }
                    items(group.transactions, key = { it.id ?: 0 }) { tx ->
                        TransactionRow(
                            transaction = tx,
                            categoryName = state.categories.find { it.id == tx.categoryId }?.name,
                            categoryColor = state.categories.find { it.id == tx.categoryId }?.color,
                            currency = state.currency,
                            onClick = { /* TODO: edit */ },
                            onLongClick = { tx.id?.let { viewModel.deleteTransaction(it) } }
                        )
                    }
                }
            } else if (!state.isLoading) {
                item {
                    EmptyState(
                        icon = Icons.Filled.AccountBalanceWallet,
                        title = stringResource(R.string.no_transactions),
                        subtitle = "Здесь появятся ваши доходы и расходы",
                        actionText = stringResource(R.string.add_transaction),
                        onAction = { showAddSheet = true }
                    )
                }
            }
        }
    }

    if (showAddSheet) {
        AddTransactionSheet(
            categories = state.categories,
            accounts = state.accounts,
            onDismiss = { showAddSheet = false },
            onCreate = { amount, type, currency, categoryId, accountId, date, note ->
                viewModel.createTransaction(amount, type, currency, categoryId, accountId, date, note)
                showAddSheet = false
            }
        )
    }
}

@Composable
private fun BalanceCard(
    balance: Double,
    currency: String,
    balancesByCurrency: List<AccountBalance> = emptyList()
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.none),
        shape = RoundedCornerShape(Radius.lg),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.primary)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                stringResource(R.string.current_balance),
                style = MaterialTheme.typography.labelLarge,
                color = AppTheme.colors.onPrimary.copy(alpha = 0.8f)
            )
            Spacer(Modifier.height(Spacing.xs))
            Text(
                formatMoney(balance, currency),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = AppTheme.colors.onPrimary
            )
            // Per-currency balances
            if (balancesByCurrency.size > 1) {
                Spacer(Modifier.height(Spacing.md))
                balancesByCurrency.forEach { ab ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            ab.currency,
                            style = MaterialTheme.typography.bodySmall,
                            color = AppTheme.colors.onPrimary.copy(alpha = 0.7f)
                        )
                        Text(
                            formatMoney(ab.balance, ab.currency),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = AppTheme.colors.onPrimary.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun PeriodSelector(
    selected: FinancePeriod,
    onSelect: (FinancePeriod) -> Unit
) {
    androidx.compose.foundation.layout.FlowRow(
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        modifier = Modifier.fillMaxWidth()
    ) {
        FinancePeriod.entries.forEach { period ->
            val label = when (period) {
                FinancePeriod.TODAY -> "Сегодня"
                FinancePeriod.WEEK -> "Неделя"
                FinancePeriod.MONTH -> "Месяц"
                FinancePeriod.YEAR -> "Год"
            }
            FilterChip(
                selected = selected == period,
                onClick = { onSelect(period) },
                label = { Text(label) }
            )
        }
    }
}

@Composable
private fun PeriodSummary(income: Double, expense: Double, net: Double, currency: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.none),
        shape = RoundedCornerShape(Radius.lg),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Spacing.lg),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            PeriodSummaryItem(
                icon = Icons.Filled.ArrowUpward,
                label = stringResource(R.string.income),
                value = formatSignedMoney(income, currency),
                color = AppTheme.colors.success
            )
            PeriodSummaryItem(
                icon = Icons.Filled.ArrowDownward,
                label = stringResource(R.string.expense),
                value = formatSignedMoney(-expense, currency),
                color = AppTheme.colors.danger
            )
            PeriodSummaryItem(
                icon = Icons.Filled.AccountBalanceWallet,
                label = stringResource(R.string.net),
                value = formatSignedMoney(net, currency),
                color = AppTheme.colors.primary
            )
        }
    }
}

@Composable
private fun PeriodSummaryItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        Spacer(Modifier.height(Spacing.xs))
        Text(label, style = MaterialTheme.typography.labelSmall, color = AppTheme.colors.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = color)
    }
}

@Composable
private fun CategoryBreakdown(
    expenses: List<CategoryExpense>,
    currency: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.none),
        shape = RoundedCornerShape(Radius.lg),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surface)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            Text(
                stringResource(R.string.expenses_by_category),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            expenses.take(6).forEach { cat ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(parseTagColor(cat.categoryColor))
                        )
                        Text(cat.categoryName, style = MaterialTheme.typography.bodyMedium)
                    }
                    Text(
                        formatMoney(cat.total, currency),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = AppTheme.colors.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun AnalyticsCard(state: FinanceUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.none),
        shape = RoundedCornerShape(Radius.lg),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surface)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            Text(
                stringResource(R.string.analytics),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            AnalyticsRow("Средние траты в день", formatMoney(state.avgDailySpending, state.baseCurrency))
            AnalyticsRow("Средние траты в месяц", formatMoney(state.avgMonthlySpending, state.baseCurrency))
            AnalyticsRow("Норма сбережений", "${state.savingsRate.toInt()}%")
            state.topIncomeSource?.let { source ->
                AnalyticsRow("Основной источник дохода", source)
            }
            state.largestExpense?.let { tx ->
                val catName = state.categories.find { it.id == tx.categoryId }?.name ?: tx.note ?: "Расход"
                AnalyticsRow("Крупнейший расход", "${formatMoney(tx.amount, tx.currency)} — $catName")
            }
        }
    }
}

@Composable
private fun AnalyticsRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = AppTheme.colors.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun TransactionRow(
    transaction: Transaction,
    categoryName: String?,
    categoryColor: String?,
    currency: String,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val isIncome = transaction.type == TransactionType.INCOME
    val isTransfer = transaction.type == TransactionType.TRANSFER
    val amountColor = when {
        isTransfer -> AppTheme.colors.info
        isIncome -> AppTheme.colors.success
        else -> AppTheme.colors.danger
    }
    val displayAmount = when {
        isTransfer -> -transaction.amount
        isIncome -> transaction.amount
        else -> -transaction.amount
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.none),
        shape = RoundedCornerShape(Radius.lg),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Spacing.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        (if (categoryColor != null) parseTagColor(categoryColor) else AppTheme.colors.primary)
                            .copy(alpha = 0.18f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (isIncome) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
                    contentDescription = null,
                    tint = if (categoryColor != null) parseTagColor(categoryColor) else AppTheme.colors.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    categoryName ?: transaction.note ?: (if (isIncome) "Доход" else "Расход"),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                transaction.note?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, color = AppTheme.colors.onSurfaceVariant, maxLines = 1)
                }
            }
            Text(
                formatSignedMoney(displayAmount, transaction.currency),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = amountColor
            )
        }
    }
}
