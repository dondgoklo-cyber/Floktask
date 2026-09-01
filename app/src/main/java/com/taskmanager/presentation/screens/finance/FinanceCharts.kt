package com.taskmanager.presentation.screens.finance

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.taskmanager.presentation.screens.finance.formatMoney
import com.taskmanager.presentation.screens.finance.formatSignedMoney
import com.taskmanager.presentation.theme.AppTheme
import com.taskmanager.presentation.screens.finance.FinanceViewModel.CategoryExpense
import com.taskmanager.presentation.theme.Elevation
import com.taskmanager.presentation.theme.Radius
import com.taskmanager.presentation.theme.Spacing

/**
 * Простой bar chart для Income vs Expenses на Compose Canvas.
 * Без сторонних библиотек.
 */
@Composable
fun IncomeExpenseBarChart(
    income: Double,
    expense: Double,
    currency: String = "RUB",
    modifier: Modifier = Modifier
) {
    val maxValue = maxOf(income, expense, 1.0)
    val incomeFraction = (income / maxValue).toFloat().coerceIn(0f, 1f)
    val expenseFraction = (expense / maxValue).toFloat().coerceIn(0f, 1f)

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.none),
        shape = RoundedCornerShape(Radius.lg),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surface)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(Spacing.lg)
        ) {
            Text(
                "Доходы vs Расходы",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(Spacing.md))

            // Income bar
            BarRow(
                label = "Доходы",
                value = formatSignedMoney(income, currency),
                fraction = incomeFraction,
                color = AppTheme.colors.success
            )
            Spacer(Modifier.height(Spacing.sm))

            // Expense bar
            BarRow(
                label = "Расходы",
                value = formatSignedMoney(-expense, currency),
                fraction = expenseFraction,
                color = AppTheme.colors.danger
            )
        }
    }
}

@Composable
private fun BarRow(
    label: String,
    value: String,
    fraction: Float,
    color: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = AppTheme.colors.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, color = color)
        }
        Spacer(Modifier.height(Spacing.xs))
        val trackColor = AppTheme.colors.surfaceVariant
        val barColor = color
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(Radius.full))
        ) {
            drawRect(color = trackColor, size = size)
            if (fraction > 0f) {
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(barColor, barColor.copy(alpha = 0.7f))
                    ),
                    size = Size(size.width * fraction, size.height)
                )
            }
        }
    }
}

/**
 * Простой pie chart для расходов по категориям на Compose Canvas.
 */
@Composable
fun CategoryPieChart(
    categories: List<CategoryExpense>,
    currency: String = "RUB",
    modifier: Modifier = Modifier
) {
    if (categories.isEmpty()) return

    val total = categories.sumOf { it.total }.coerceAtLeast(0.001)
    val colors = listOf(
        AppTheme.colors.primary,
        AppTheme.colors.success,
        AppTheme.colors.warning,
        AppTheme.colors.info,
        AppTheme.colors.danger,
        AppTheme.colors.secondary
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.none),
        shape = RoundedCornerShape(Radius.lg),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surface)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(Spacing.lg)
        ) {
            Text(
                "Расходы по категориям",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(Spacing.md))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pie chart canvas
                val chartColors = colors
                Canvas(
                    modifier = Modifier.size(100.dp)
                ) {
                    var startAngle = -90f
                    categories.take(6).forEachIndexed { index, cat ->
                        val sweep = (cat.total / total * 360).toFloat()
                        drawArc(
                            color = chartColors[index % chartColors.size],
                            startAngle = startAngle,
                            sweepAngle = sweep,
                            useCenter = true
                        )
                        startAngle += sweep
                    }
                }

                Spacer(Modifier.size(Spacing.md))

                // Legend
                Column(modifier = Modifier.weight(1f)) {
                    categories.take(6).forEachIndexed { index, cat ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(Spacing.xs)
                        ) {
                            Canvas(modifier = Modifier.size(10.dp)) {
                                drawCircle(colors[index % colors.size])
                            }
                            Text(
                                "${cat.categoryName} (${(cat.total / total * 100).toInt()}%)",
                                style = MaterialTheme.typography.bodySmall,
                                color = AppTheme.colors.onSurfaceVariant
                            )
                        }
                        Spacer(Modifier.height(2.dp))
                    }
                }
            }
        }
    }
}
