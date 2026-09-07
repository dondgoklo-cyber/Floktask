package com.taskmanager.presentation.screens.insights

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.taskmanager.R
import com.taskmanager.presentation.components.EmptyState
import com.taskmanager.presentation.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.statistics),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppTheme.colors.surface
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            EmptyState(
                icon = Icons.Filled.BarChart,
                title = "Аналитика скоро появится",
                message = "Здесь будет статистика по задачам, фокусу, привычкам и финансам",
                actionLabel = "Создать задачу",
                onAction = { },
                modifier = Modifier
            )
        }
    }
}
