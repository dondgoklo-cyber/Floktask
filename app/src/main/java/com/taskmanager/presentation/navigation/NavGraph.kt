package com.taskmanager.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ViewKanban
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.taskmanager.presentation.screens.focus.FocusScreen
import com.taskmanager.presentation.screens.insights.InsightsScreen
import com.taskmanager.presentation.screens.inbox.InboxScreen
import com.taskmanager.presentation.screens.today.TodayScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                Screen.bottomNavItems.forEach { screen ->
                    val selected =
                        currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(stringResource(screen.labelRes)) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Today.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Today.route) {
                TodayScreen()
            }
            composable(Screen.Plan.route) {
                InboxScreen(onEditTask = { })
            }
            composable(Screen.Focus.route) {
                FocusScreen()
            }
            composable(Screen.Insights.route) {
                InsightsScreen()
            }
        }
    }
}

private val Screen.icon: ImageVector
    get() = when (this) {
        Screen.Today -> Icons.Filled.CheckCircle
        Screen.Plan -> Icons.Filled.CalendarMonth
        Screen.Focus -> Icons.Filled.Timer
        Screen.Insights -> Icons.Filled.BarChart
        Screen.Projects -> Icons.Filled.Folder
        Screen.Finance -> Icons.Filled.AccountBalanceWallet
        Screen.Notes -> Icons.Filled.Description
        Screen.Habits -> Icons.Filled.Repeat
        Screen.Calendar -> Icons.Filled.CalendarMonth
        Screen.Upcoming -> Icons.Filled.CalendarMonth
        Screen.Inbox -> Icons.Filled.Inbox
        Screen.Search -> Icons.Filled.Search
        Screen.Kanban -> Icons.Filled.ViewKanban
        Screen.Tags -> Icons.Filled.Label
        Screen.Eisenhower -> Icons.Filled.ViewKanban
        Screen.Profile -> Icons.Filled.Person
        Screen.Settings -> Icons.Filled.Settings
    }
