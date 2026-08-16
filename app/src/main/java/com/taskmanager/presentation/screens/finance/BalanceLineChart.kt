package com.taskmanager.presentation.screens.finance

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.taskmanager.presentation.theme.AppTheme
import com.taskmanager.presentation.theme.Elevation
import com.taskmanager.presentation.theme.Radius
import com.taskmanager.presentation.theme.Spacing

/**
 * Line chart для баланса на Compose Canvas.
 * Показывает изменение баланса по дням за выбранный период.
 */
@Composable
fun BalanceLineChart(
    dailyBalances: List<Double>,
    currency: String = "RUB",
    modifier: Modifier = Modifier
) {
    if (dailyBalances.size < 2) return

    val maxValue = (dailyBalances.maxOrNull() ?: 0.0).coerceAtLeast(1.0)
    val minValue = (dailyBalances.minOrNull() ?: 0.0).coerceAtMost(0.0)
    val range = (maxValue - minValue).coerceAtLeast(1.0)
    
    val lineColor = AppTheme.colors.primary
    val gradientColor = AppTheme.colors.primary
    val trackColor = AppTheme.colors.surfaceVariant

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
                "Баланс по дням",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(Spacing.md))

            val safeLineColor = lineColor
            val safeGradientColor = gradientColor
            val safeTrackColor = trackColor
            val safeMin = minValue
            val safeRange = range
            
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(Radius.md))
            ) {
                val w = size.width
                val h = size.height
                val stepX = if (dailyBalances.size > 1) w / (dailyBalances.size - 1) else w

                // Zero line
                val zeroY = h - ((0 - safeMin) / safeRange).toFloat() * h
                if (zeroY in 0f..h) {
                    drawLine(
                        color = safeTrackColor,
                        start = Offset(0f, zeroY),
                        end = Offset(w, zeroY),
                        strokeWidth = 1f
                    )
                }

                // Build path
                val path = Path()
                dailyBalances.forEachIndexed { index, value ->
                    val x = index * stepX
                    val y = h - ((value - safeMin) / safeRange).toFloat() * h
                    if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }

                // Draw line
                drawPath(
                    path = path,
                    color = safeLineColor,
                    style = Stroke(width = 3f, cap = StrokeCap.Round)
                )

                // Fill area under curve
                val fillPath = Path()
                dailyBalances.forEachIndexed { index, value ->
                    val x = index * stepX
                    val y = h - ((value - safeMin) / safeRange).toFloat() * h
                    if (index == 0) fillPath.moveTo(x, y) else fillPath.lineTo(x, y)
                }
                fillPath.lineTo(w, h)
                fillPath.lineTo(0f, h)
                fillPath.close()

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            safeGradientColor.copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    )
                )
            }
            
            Spacer(Modifier.height(Spacing.xs))
            Text(
                "Текущий: ${formatMoney(dailyBalances.last(), currency)}",
                style = MaterialTheme.typography.bodySmall,
                color = AppTheme.colors.onSurfaceVariant
            )
        }
    }
}
