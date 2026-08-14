package com.taskmanager.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.taskmanager.R
import com.taskmanager.presentation.screens.calendar.CalendarScreen
import com.taskmanager.presentation.screens.focus.FocusScreen
import com.taskmanager.presentation.screens.habits.HabitsScreen
import com.taskmanager.presentation.screens.more.MoreScreen
import com.taskmanager.presentation.screens.projects.ProjectsScreen
import com.taskmanager.presentation.screens.tasks.TaskEditScreen
import com.taskmanager.presentation.screens.today.TodayScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val currentRoute = currentDestination?.route
    val navRoutes = Screen.bottomNavItems.map { it.route }.toSet()
    val showBottomBar = navRoutes.contains(currentRoute)

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
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
                    // Кнопка "More" — всегда последняя
                    NavigationBarItem(
                        selected = currentRoute == Screen.More.route,
                        onClick = { navController.navigate(Screen.More.route) },
                        icon = { Icon(Icons.Filled.MoreHoriz, contentDescription = null) },
                        label = { Text(stringResource(R.string.more)) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Today.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(animationSpec = tween(120)) },
            exitTransition = { fadeOut(animationSpec = tween(80)) },
            popEnterTransition = { fadeIn(animationSpec = tween(120)) },
            popExitTransition = { fadeOut(animationSpec = tween(80)) }
        ) {
            composable(Screen.Today.route) {
                TodayScreen(
                    onTaskClick = { taskId -> navController.navigate(Screen.TaskEdit.buildRoute(taskId)) },
                    onAddTaskClick = { navController.navigate(Screen.TaskEditNew.route) }
                )
            }
            composable(Screen.Projects.route) { ProjectsScreen() }
            composable(Screen.Calendar.route) { CalendarScreen() }
            composable(Screen.Habits.route) { HabitsScreen() }
            composable(Screen.Focus.route) { FocusScreen() }
            composable(Screen.More.route) {
                MoreScreen(
                    onNavigate = { route -> navController.navigate(route) }
                )
            }

            composable(Screen.TaskEditNew.route) {
                TaskEditScreen(onBack = { navController.popBackStack() })
            }
            composable(
                route = Screen.TaskEdit.route,
                arguments = listOf(navArgument(Screen.TaskEdit.ARG_TASK_ID) { type = NavType.LongType })
            ) {
                TaskEditScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}

private val Screen.icon: ImageVector
    get() = when (this) {
        Screen.Today -> Icons.Filled.Checklist
        Screen.Projects -> Icons.Filled.Folder
        Screen.Calendar -> Icons.Filled.CalendarMonth
        Screen.Habits -> Icons.Filled.Checklist
        Screen.Focus -> Icons.Filled.Bolt
        Screen.More,
        Screen.Inbox,
        Screen.Upcoming,
        Screen.Eisenhower,
        Screen.Statistics,
        Screen.Settings,
        Screen.TaskEditNew,
        Screen.TaskEdit -> Icons.Filled.MoreHoriz
    }
