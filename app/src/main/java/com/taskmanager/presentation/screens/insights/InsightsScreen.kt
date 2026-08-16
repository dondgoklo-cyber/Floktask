package com.taskmanager.presentation.screens.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.taskmanager.domain.model.DayCount
import com.taskmanager.domain.model.ProductivityStats
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val SHORT_DATE = DateTimeFormatter.ofPattern("E dd/MM")

@Composable
fun InsightsScreen(
    viewModel: InsightsViewModel = hiltViewModel()
) {
    val stats by viewModel.stats.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Insights", style = MaterialTheme.typography.headlineSmall)
        }

        item {
            StreaksCard(stats)
        }

        item {
            WeeklyReportCard(stats?.weeklyReport.orEmpty())
        }

        item {
            HeatmapCard(stats)
        }
    }
}

@Composable
private fun StreaksCard(stats: ProductivityStats?) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Streaks", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatColumn("Current", "${stats?.currentStreak ?: 0}")
                StatColumn("Longest", "${stats?.longestStreak ?: 0}")
                StatColumn("Completed", "${stats?.totalCompleted ?: 0}")
            }
        }
    }
}

@Composable
private fun StatColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
    }
}

@Composable
private fun WeeklyReportCard(weekly: List<DayCount>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Last 7 days", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            if (weekly.isEmpty()) {
                Text(
                    "No data yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(top = 8.dp)
                )
            } else {
                weekly.forEach { day ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            day.date.format(SHORT_DATE),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            "${day.completed} done",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeatmapCard(stats: ProductivityStats?) {
    val data = stats?.dailyCompletion ?: emptyMap()
    val today = LocalDate.now()
    // show last 5 weeks as columns of 7 days
    val weeks = 7
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Activity (last 7 weeks)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (w in 0 until weeks) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        for (d in 0 until 7) {
                            val date = today.minusDays(((weeks - 1 - w) * 7L + (6 - d)))
                            val count = data[date] ?: 0
                            HeatmapCell(count)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeatmapCell(count: Int) {
    val color = when {
        count == 0 -> MaterialTheme.colorScheme.surfaceVariant
        count <= 2 -> Color(0xFFC8E6C9)
        count <= 4 -> Color(0xFF81C784)
        count <= 6 -> Color(0xFF4CAF50)
        else -> Color(0xFF2E7D32)
    }
    Box(
        modifier = Modifier
            .size(14.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(color)
    )
}
