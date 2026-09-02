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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.Stroke
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.taskmanager.presentation.screens.finance.formatMoney
import com.taskmanager.presentation.theme.AppTheme
import com.taskmanager.presentation.theme.Elevation
import com.taskmanager.presentation.theme.Radius
import com.taskmanager.presentation.theme.Spacing
import java.math.BigDecimal

/**
 * Line chart для баланса на Compose Canvas.
 * Показывает изменение баланса за выбранный период по дням.
 */
@Composable
fun BalanceLineChart(
    dailyBalances: List<BigDecimal>,
    currency: String = "RUB",
    modifier: Modifier = Modifier
) {
    if (dailyBalances.size < 2) return

    val maxValue = (dailyBalances.maxOrNull() ?: BigDecimal.ZERO).coerceAtLeast(BigDecimal.ONE)
    val minValue = (dailyBalances.minOrNull() ?: BigDecimal.ZERO).coerceAtMost(BigDecimal.ZERO)
    val range = (maxValue - minValue).coerceAtLeast(BigDecimal.ONE)
    
    val points = mutableListOf<Offset>()
    val textOffsets = mutableListOf<Offset>()

    dailyBalances.forEachIndexed { index, balance ->
        val x = (index.toFloat() / (dailyBalances.size - 1)) * size.width
        val y = size.height - ((balance.toDouble() - minValue.toDouble()) / range.toDouble() * size.height).toFloat()
        points.add(Offset(x, y))
        textOffsets.add(Offset(x, y - 30f))
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.none),
        shape = RoundedCornerShape(Radius.lg),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Динамика баланса",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(Spacing.md))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(Radius.sm))
            ) {
                val canvasSize = size
                if (points.isNotEmpty()) {
                    // Draw line
                    val path = Path().apply {
                        moveTo(points[0].x, points[0].y)
                        for (i in 1 until points.size) {
                            lineTo(points[i].x, points[i].y)
                        }
                    }

                    drawPath(
                        path = path,
                        color = AppTheme.colors.primary,
                        style = Stroke(width = 4f, cap = StrokeCap.Round)
                    )

                    // Draw fill
                    val fillPath = Path().apply {
                        moveTo(points[0].x, points[0].y)
                        for (i in 1 until points.size) {
                            lineTo(points[i].x, points[i].y)
                        }
                        lineTo(points.last().x, size.height)
                        lineTo(points[0].x, size.height)
                        close()
                    }

                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                AppTheme.colors.primary.copy(alpha = 0.3f),
                                AppTheme.colors.primary.copy(alpha = 0.0f)
                            ),
                            startY = points.last().y,
                            endY = size.height
                        )
                    )

                    // Draw points
                    points.forEach { offset ->
                        drawCircle(
                            color = AppTheme.colors.primary,
                            radius = 6f,
                            center = offset
                        )
                    }

                    // Draw value labels
                    points.forEachIndexed { index, offset ->
                        val balance = dailyBalances[index]
                        drawContext.canvas.nativeCanvas.apply {
                            drawText(
                                formatMoney(balance, currency),
                                offset.x,
                                offset.y - 10f,
                                android.graphics.Paint().apply {
                                    textSize = 30f
                                    color = android.graphics.Color.WHITE
                                    textAlign = android.graphics.Paint.Align.CENTER
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(Spacing.sm))
            Text(
                "Свайпните для деталей →",
                style = MaterialTheme.typography.bodySmall,
                color = AppTheme.colors.textSecondary
            )
        }
    }
}
