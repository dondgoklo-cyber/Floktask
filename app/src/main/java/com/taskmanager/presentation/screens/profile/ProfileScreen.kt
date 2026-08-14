package com.taskmanager.presentation.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.taskmanager.R
import com.taskmanager.presentation.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.profile)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppTheme.colors.surface
                )
            )
        }
    ) { padding ->
        when (val s = state) {
            ProfileState.Loading -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            is ProfileState.Error -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { Text(s.message) }

            is ProfileState.Loaded -> {
                val stats = s.stats
                val (into, span) = progressToNextLevel(stats)
                val progress = if (span == 0L) 0f else (into.toFloat() / span.toFloat()).coerceIn(0f, 1f)
                val achievements = viewModel.achievementsFor(stats.unlockedAchievementIds)

                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Card(
                            Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(Modifier.padding(20.dp)) {
                                Text(
                                    stringResource(R.string.level) + " " + stats.level,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    stats.totalPoints.toString() + " " + stringResource(R.string.points),
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(Modifier.padding(top = 12.dp))
                                LinearProgressIndicator(
                                    progress = progress,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(Modifier.padding(top = 8.dp))
                                Text(
                                    "$into / $span → " + stringResource(R.string.level) + " " + (stats.level + 1),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatColumn(stringResource(R.string.completed), stats.completedTasks.toString())
                            StatColumn(stringResource(R.string.streak), stats.streak.toString())
                            StatColumn(
                                stringResource(R.string.badges),
                                "${stats.unlockedAchievementIds.size}/${achievements.size}"
                            )
                        }
                    }
                    item {
                        Text(
                            stringResource(R.string.achievements),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    items(achievements, key = { it.achievement.id }) { display ->
                        AchievementRow(display)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun AchievementRow(display: AchievementDisplay) {
    Card(
        Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    display.achievement.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (display.unlocked) MaterialTheme.colorScheme.onSurface else AppTheme.colors.onSurfaceVariant
                )
                Text(
                    display.achievement.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = AppTheme.colors.onSurfaceVariant
                )
            }
            Text(
                "+${display.achievement.pointsReward}",
                style = MaterialTheme.typography.titleSmall,
                color = if (display.unlocked) MaterialTheme.colorScheme.primary else AppTheme.colors.onSurfaceVariant
            )
        }
    }
}
