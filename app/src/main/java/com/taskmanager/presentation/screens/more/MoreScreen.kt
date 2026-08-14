package com.taskmanager.presentation.screens.more

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.taskmanager.R
import com.taskmanager.presentation.navigation.Screen
import com.taskmanager.presentation.theme.AppTheme
import com.taskmanager.presentation.theme.Spacing

private data class MoreItem(
    val icon: ImageVector,
    val titleRes: Int,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        MoreItem(Icons.Filled.Inbox, R.string.inbox, Screen.Inbox.route),
        MoreItem(Icons.Filled.CalendarMonth, R.string.upcoming, Screen.Upcoming.route),
        MoreItem(Icons.Filled.GridView, R.string.eisenhower_matrix, Screen.Eisenhower.route),
        MoreItem(Icons.Filled.Person, R.string.profile, Screen.Profile.route),
        MoreItem(Icons.Filled.Person, R.string.settings, Screen.Settings.route)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.more)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppTheme.colors.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            items(items) { item ->
                ListItem(
                    headlineContent = { Text(stringResource(item.titleRes)) },
                    leadingContent = { Icon(item.icon, contentDescription = null) },
                    trailingContent = {
                        IconButton(onClick = { onNavigate(item.route) }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                HorizontalDivider(color = AppTheme.colors.divider)
            }
        }
    }
}
