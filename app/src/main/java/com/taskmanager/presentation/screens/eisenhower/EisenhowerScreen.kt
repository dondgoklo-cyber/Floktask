package com.taskmanager.presentation.screens.eisenhower

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.taskmanager.domain.model.EisenhowerQuadrant
import com.taskmanager.domain.model.Task

@Composable
fun EisenhowerScreen(
    viewModel: EisenhowerViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Smart Assistant banner
        state.suggestion?.let { suggestion ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        "Smart Assistant",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Text(
                        suggestion.message,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    suggestion.nextTask?.let { task ->
                        Text(
                            "→ ${task.title}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }

        // 2x2 matrix
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuadrantCard(
                quadrant = EisenhowerQuadrant.Q1,
                tasks = state.buckets[EisenhowerQuadrant.Q1].orEmpty(),
                containerColor = Color(0xFFFFCDD2),
                modifier = Modifier.weight(1f)
            )
            QuadrantCard(
                quadrant = EisenhowerQuadrant.Q2,
                tasks = state.buckets[EisenhowerQuadrant.Q2].orEmpty(),
                containerColor = Color(0xFFC5E1A5),
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuadrantCard(
                quadrant = EisenhowerQuadrant.Q3,
                tasks = state.buckets[EisenhowerQuadrant.Q3].orEmpty(),
                containerColor = Color(0xFFFFF59D),
                modifier = Modifier.weight(1f)
            )
            QuadrantCard(
                quadrant = EisenhowerQuadrant.Q4,
                tasks = state.buckets[EisenhowerQuadrant.Q4].orEmpty(),
                containerColor = Color(0xFFB0BEC5),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun QuadrantCard(
    quadrant: EisenhowerQuadrant,
    tasks: List<Task>,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    val (title, subtitle) = when (quadrant) {
        EisenhowerQuadrant.Q1 -> "Q1 · Do now" to "Urgent + Important"
        EisenhowerQuadrant.Q2 -> "Q2 · Schedule" to "Important, not urgent"
        EisenhowerQuadrant.Q3 -> "Q3 · Delegate" to "Urgent, not important"
        EisenhowerQuadrant.Q4 -> "Q4 · Later" to "Neither"
    }
    Surface(
        modifier = modifier,
        color = containerColor,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Text(
                subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Black.copy(alpha = 0.6f)
            )
            Text(
                "${tasks.size} task(s)",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Black.copy(alpha = 0.6f),
                modifier = Modifier.padding(vertical = 4.dp)
            )
            LazyColumn {
                items(tasks, key = { it.id ?: it.title.hashCode().toLong() }) { task ->
                    Text(
                        task.title,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
    }
}
