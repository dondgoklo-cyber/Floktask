package com.taskmanager.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.List
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.taskmanager.presentation.screens.calendar.CalendarScreen
import com.taskmanager.presentation.screens.onboarding.OnboardingScreen
import com.taskmanager.presentation.screens.onboarding.OnboardingViewModel
import com.taskmanager.presentation.screens.projects.ProjectsScreen
import com.taskmanager.presentation.screens.tasks.TasksScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val onboardingViewModel: OnboardingViewModel = hiltViewModel()
    val onboardingState by onboardingViewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            if (currentDestination?.route != Screen.Onboarding.route) {
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
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (onboardingState.isCompleted) Screen.Tasks.route
            else Screen.Onboarding.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    viewModel = onboardingViewModel,
                    onFinish = {
                        navController.navigate(Screen.Tasks.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Tasks.route) {
                TasksScreen(
                    onTaskClick = { },
                    onAddTaskClick = { }
                )
            }
            composable(Screen.Projects.route) {
                ProjectsScreen()
            }
            composable(Screen.Calendar.route) {
                CalendarScreen()
            }
        }
    }
}

private val Screen.icon: ImageVector
    get() = when (this) {
        Screen.Tasks -> Icons.Filled.List
        Screen.Projects -> Icons.Filled.Folder
        Screen.Calendar -> Icons.Filled.DateRange
        Screen.Onboarding -> Icons.Filled.List
    }
